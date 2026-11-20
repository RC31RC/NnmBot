package com.nnm.bot.warframe.runtime.parser.steelincursion;

import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.steelincursion.SteelIncursionEntryDTO;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/** 将 browse.wf 每日钢铁侵袭行解析为星图任务并连接已入库官方代码簿。 */
@Component
public class WarframeSteelIncursionParser {
    private static final int DAILY_MISSION_COUNT = 6;
    @Resource
    private WarframeSteelIncursionProperties properties;
    @Resource
    private WarframeWorldStateCodebookService codebook;
    @Resource
    private Clock warframeClock;

    /** @param payload 上游文本。 @return 按日期与节点排序的排期。 */
    public List<SteelIncursionEntryDTO> parse(String payload) {
        if (CharSequenceUtil.isBlank(payload))
            throw new IllegalArgumentException("Empty steel incursion schedule");
        List<SteelIncursionEntryDTO> entries = new ArrayList<>();
        Set<Instant> activations = new HashSet<>();
        Instant windowStart =
            LocalDate.ofInstant(warframeClock.instant(), ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant windowEnd = windowStart.plus(properties.getDataWindow());
        int lineNumber = 0;
        for (String rawLine : payload.split("\\R", -1)) {
            lineNumber++;
            String line = CharSequenceUtil.trim(rawLine);
            if (CharSequenceUtil.isBlank(line) || line.startsWith("#") || line.startsWith("//"))
                continue;
            String[] sections = line.split(";", -1);
            if (sections.length != 2 || CharSequenceUtil.isBlank(sections[0]))
                throw invalidLine(lineNumber);
            Instant activation;
            try {
                activation = Instant.ofEpochSecond(Long.parseLong(CharSequenceUtil.trim(sections[0])));
            } catch (RuntimeException exception) {
                throw new IllegalArgumentException("Invalid steel incursion timestamp at line " + lineNumber,
                    exception);
            }
            if (activation.getEpochSecond() <= 0)
                throw invalidLine(lineNumber);
            if (activation.isBefore(windowStart) || !activation.isBefore(windowEnd))
                continue;
            String[] nodes = sections[1].split(",", -1);
            if (!activations.add(activation) || nodes.length != DAILY_MISSION_COUNT
                || entries.size() >= properties.getDataWindow().toDays() * DAILY_MISSION_COUNT)
                throw invalidLine(lineNumber);
            Set<String> nodeIdentities = new HashSet<>();
            for (String rawNode : nodes) {
                String nodeReference = CharSequenceUtil.trim(rawNode);
                if (CharSequenceUtil.isBlank(nodeReference) || !nodeIdentities.add(nodeReference))
                    throw invalidLine(lineNumber);
                entries.add(resolve(activation, nodeReference));
            }
        }
        return entries.stream().sorted(Comparator.comparing(SteelIncursionEntryDTO::activation)).toList();
    }

    /** @param activation 当日开始时间。 @param nodeReference 官方节点引用。 @return 解析后的节点任务。 */
    private SteelIncursionEntryDTO resolve(Instant activation, String nodeReference) {
        var node = codebook.findNode(nodeReference).orElse(null);
        String nodeName = node == null ? nodeReference : node.displayName();
        String missionType = node == null ? ""
            : codebook.findMissionType(node.relatedReference()).map(entry -> entry.displayName()).orElse("");
        String enemy = node == null ? ""
            : codebook.findFaction(node.factionReference()).map(entry -> entry.displayName()).orElse("");
        String planet = node == null ? "" : extractPlanet(nodeName);
        return new SteelIncursionEntryDTO("sp-incursions:" + activation.getEpochSecond() + ":" + nodeReference,
            activation, activation.plus(Duration.ofDays(1)), nodeName, nodeReference, planet, missionType, enemy);
    }

    /** @param nodeName 官方节点展示名。 @return 括号内的星球名称；未提供时为空字符串。 */
    private static String extractPlanet(String nodeName) {
        String normalized = nodeName.replace('（', '(').replace('）', ')');
        int opening = normalized.lastIndexOf('(');
        return opening >= 0 && normalized.endsWith(")")
            ? CharSequenceUtil.trim(normalized.substring(opening + 1, normalized.length() - 1)) : "";
    }

    /** @param lineNumber 输入行号。 @return 含行号的协议校验异常。 */
    private static IllegalArgumentException invalidLine(int lineNumber) {
        return new IllegalArgumentException("Invalid steel incursion row at line " + lineNumber);
    }
}
