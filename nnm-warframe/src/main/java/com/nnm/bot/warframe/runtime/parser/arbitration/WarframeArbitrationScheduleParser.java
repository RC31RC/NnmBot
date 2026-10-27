package com.nnm.bot.warframe.runtime.parser.arbitration;

import com.fasterxml.jackson.core.JsonProcessingException;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.rawdata.arbitration.ArbitrationScheduleRawData;
import com.nnm.bot.warframe.runtime.rawdata.arbitration.ArbitrationScheduleRawData.EntryRawData;
import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationPullProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Component
public class WarframeArbitrationScheduleParser {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeArbitrationPullProperties properties;
    @Resource
    private WarframeWorldStateCodebookService codebook;

    /** 独立绑定器只校验该输入，不影响共用的 HTTP 序列化配置。 */
    private ObjectMapper inputMapper;

    /**
     * 初始化组件在运行期间复用的状态。
     */
    @PostConstruct
    void initialize() {
        inputMapper = WarframeInputBinding.strictMapper(objectMapper);
    }

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param payload 外部响应或消息的载荷。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<ArbitrationScheduleEntryDTO> parse(String payload) {
        if (StrUtil.isBlank(payload)) {
            throw new IllegalArgumentException("Empty arbitration schedule");
        }
        String body = payload.trim();
        return body.startsWith("[") || body.startsWith("{") ? parseJson(body) : parseText(body);
    }

    /**
     * 将 JSON 协议响应绑定为明确的记录类型。
     *
     * @param body 外部接口返回的响应正文。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<ArbitrationScheduleEntryDTO> parseJson(String body) {
        try {
            List<EntryRawData> rows = body.startsWith("[")
                    ? inputMapper.readerForListOf(EntryRawData.class).readValue(body)
                    : inputMapper.readValue(body, ArbitrationScheduleRawData.class).getEntries();
            if (CollUtil.isEmpty(rows) || rows.size() > properties.getMaxRecords()) {
                throw new IllegalArgumentException("仲裁排期集合为空或超过上限");
            }
            List<ArbitrationScheduleEntryDTO> entries = new ArrayList<>(rows.size());
            Set<String> ids = new HashSet<>();
            Set<Instant> activations = new HashSet<>();
            for (EntryRawData row : rows) {
                String id = required(row == null ? null : row.getId(), "id", 100);
                Instant activation = instant(row.getActivation(), "activation");
                Instant expiry = instant(row.getExpiry(), "expiry");
                if (!ids.add(id) || !activations.add(activation)
                        || !Duration.between(activation, expiry).equals(Duration.ofHours(1))) {
                    throw new IllegalArgumentException("Invalid or duplicate arbitration interval: " + id);
                }
                String node = display(row.getNodeZh(), row.getNode(), "node", 200);
                String mission = display(row.getMissionTypeZh(), row.getMissionType(), "missionType", 100);
                String enemy = display(row.getFactionZh(), row.getFaction(), "faction", 100);
                String planet = display(row.getPlanetZh(), row.getPlanet(), "planet", 100);
                var bounds = row.getBounds();
                entries.add(new ArbitrationScheduleEntryDTO(id, activation, expiry, node, mission, enemy, planet,
                        bounds == null ? "{}" : objectMapper.writeValueAsString(bounds)));
            }
            return entries.stream().sorted(Comparator.comparing(ArbitrationScheduleEntryDTO::activation)).toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid arbitration schedule JSON", exception);
        }
    }

    /**
     * 按上游文本协议读取并校验每条数据。
     *
     * @param body 外部接口返回的响应正文。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<ArbitrationScheduleEntryDTO> parseText(String body) {
        TreeMap<Instant, String> rows = new TreeMap<>();
        int lineNumber = 0;
        for (String rawLine : body.split("\\R", -1)) {
            lineNumber++;
            String line = rawLine.trim();
            if (StrUtil.isEmpty(line) || line.startsWith("#") || line.startsWith("//")) continue;
            String[] columns = line.split(",", -1);
            if (columns.length != 2 || StrUtil.isBlank(columns[0]) || StrUtil.isBlank(columns[1])) {
                throw new IllegalArgumentException("Invalid arbitration line " + lineNumber);
            }
            try {
                Instant activation = Instant.ofEpochSecond(Long.parseLong(columns[0].trim()));
                if (activation.getEpochSecond() <= 0 || rows.putIfAbsent(activation, columns[1].trim()) != null
                        || rows.size() > properties.getMaxRecords()) {
                    throw new IllegalArgumentException("Invalid or duplicate arbitration time at line " + lineNumber);
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid arbitration timestamp at line " + lineNumber, exception);
            }
        }
        if (CollUtil.isEmpty(rows)) throw new IllegalArgumentException("Arbitration source contains no rows");
        List<ArbitrationScheduleEntryDTO> entries = new ArrayList<>(rows.size());
        for (var row : rows.entrySet()) entries.add(resolveTextEntry(row.getKey(), row.getValue()));
        return List.copyOf(entries);
    }

    /**
     * 结合已采纳的节点代码簿，将文本排期转换为完整仲裁记录。
     *
     * @param activation 开始时间。
     * @param nodeReference 节点引用。
     * @return 仲裁排期条目对象。
     */
    private ArbitrationScheduleEntryDTO resolveTextEntry(Instant activation, String nodeReference) {
        WarframeWorldStateCodebookEntryDTO node = codebook.findNode(nodeReference)
                .orElseThrow(() -> new IllegalArgumentException("Unknown arbitration node: " + nodeReference));
        WarframeWorldStateCodebookEntryDTO mission = codebook.findMissionType(node.relatedReference())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown arbitration mission type: " + node.relatedReference()));
        WarframeWorldStateCodebookEntryDTO faction = codebook.findFaction(node.factionReference())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown arbitration faction: " + node.factionReference()));
        String planet = extractPlanet(node.displayName());
        if (StrUtil.isBlank(planet)) throw new IllegalArgumentException("Arbitration node has no planet: " + nodeReference);
        return new ArbitrationScheduleEntryDTO("arbys:" + activation.getEpochSecond(), activation,
                activation.plus(Duration.ofHours(1)), node.displayName(), mission.displayName(),
                faction.displayName(), planet, "{}");
    }

    /**
     * 字符串已由 Jackson 按字段绑定，业务层只检查时间和长度。
     *
     * @param value 待校验或规范化的文本。
     * @param field 已经声明的字段或字段标识。
     * @return 时间点对象。
     */
    private Instant instant(String value, String field) {
        return Instant.parse(required(value, field, 40));
    }

    /**
     * 校验必填文本和最大长度。
     *
     * @param value 待校验或规范化的文本。
     * @param field 已经声明的字段或字段标识。
     * @param maxLength 最大长度。
     * @return 校验必填文本和最大长度对应的文本。
     */
    private String required(String value, String field, int maxLength) {
        if (StrUtil.isBlank(value) || value.length() > maxLength) {
            throw new IllegalArgumentException("仲裁字段为空或过长：" + field);
        }
        return value.trim();
    }

    /**
     * 优先选择有效本地化文本并校验显示字段。
     *
     * @param localized 本地化。
     * @param original 原始。
     * @param field 已经声明的字段或字段标识。
     * @param maxLength 最大长度。
     * @return 优先选择有效本地化文本并校验显示字段对应的文本。
     */
    private String display(String localized, String original, String field, int maxLength) {
        return required(StrUtil.isNotBlank(localized) ? localized : original, field, maxLength);
    }

    /**
     * 从明确包含星球括号的节点显示名提取星球名称。
     *
     * @param nodeName 节点名称。
     * @return 从明确包含星球括号的节点显示名提取星球名称对应的文本。
     */
    private static String extractPlanet(String nodeName) {
        String normalized = nodeName.replace('（', '(').replace('）', ')');
        int opening = normalized.lastIndexOf('(');
        return opening >= 0 && normalized.endsWith(")")
                ? normalized.substring(opening + 1, normalized.length() - 1).trim() : "";
    }
}
