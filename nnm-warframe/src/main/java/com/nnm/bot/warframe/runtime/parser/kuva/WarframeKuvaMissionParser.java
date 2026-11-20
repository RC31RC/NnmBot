package com.nnm.bot.warframe.runtime.parser.kuva;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.constant.WarframeInvasionMissionType;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.runtime.pojo.dto.kuva.KuvaMissionDTO;
import com.nnm.bot.warframe.runtime.rawdata.kuva.KuvaLogRawData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 解析当前有效赤毒任务，并使用官方代码簿解释节点与任务类型。 */
@Component
@Slf4j
public class WarframeKuvaMissionParser {
    private static final int MAX_LOG_ENTRIES = 50_000;
    private static final Duration START_BUFFER = Duration.ofMinutes(5);
    private static final Duration END_BUFFER = Duration.ofMinutes(1);
    private static final Duration MAX_MISSION_DURATION = Duration.ofHours(2);
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeWorldStateCodebookService codebook;
    @Resource
    private Clock warframeClock;
    private ObjectReader inputReader;

    /** 初始化严格但允许协议扩展字段的读取器。 */
    @PostConstruct
    void initialize() {
        inputReader =
            WarframeInputBinding.strictMapper(objectMapper).readerFor(new TypeReference<List<KuvaLogRawData>>() {});
    }

    /** @param body 上游 JSON 字节。 @return 当前有效的去重任务。 @throws IOException 输入不符合 JSON 合同时抛出。 */
    public List<KuvaMissionDTO> parse(byte[] body) throws IOException {
        List<KuvaLogRawData> rows = inputReader.readValue(body);
        if (ObjUtil.isNull(rows) || rows.size() > MAX_LOG_ENTRIES)
            throw new IllegalArgumentException("Kuva mission response is empty or exceeds the record limit");
        Instant now = warframeClock.instant();
        Map<String, KuvaMissionDTO> missions = new LinkedHashMap<>();
        for (KuvaLogRawData row : rows) {
            if (ObjUtil.isNull(row) || ObjUtil.isNull(row.missiontype())
                || !row.missiontype().startsWith(WarframeWorldStateDisplayConstants.KUVA_MISSION_PREFIX)
                || CharSequenceUtil.isBlank(row.solnode()) || ObjUtil.isNull(row.solnodedata())
                || ObjUtil.isNull(row.solnodedata().type()))
                continue;
            try {
                Instant activation = Instant.parse(row.start()).plus(START_BUFFER);
                Instant expiry = Instant.parse(row.end()).minus(END_BUFFER);
                if (now.isBefore(activation) || !now.isBefore(expiry)
                    || expiry.isAfter(activation.plus(MAX_MISSION_DURATION)))
                    continue;
                String missionCode = WarframeInvasionMissionType.officialCode(row.solnodedata().type());
                String nodeName = row.solnode();
                String missionName = missionCode;
                try {
                    var node = codebook.findNode(row.solnode()).orElse(null);
                    if (ObjUtil.isNotNull(node)) {
                        nodeName = node.displayName();
                        missionName = codebook.findMissionType(node.relatedReference())
                            .map(entry -> entry.displayName()).orElse(missionCode);
                    }
                } catch (RuntimeException exception) {
                    // 静态代码簿不可用时保留结构化任务和来源引用，不因此清空成功动态数据。
                    log.debug("Kuva mission codebook lookup unavailable for {}", row.solnode(), exception);
                }
                missions.putIfAbsent(row.solnode(),
                    new KuvaMissionDTO(nodeName, row.solnode(), missionName, activation, expiry));
            } catch (RuntimeException invalidRow) {
                // 单条日志时间无效时跳过该行，不丢弃同批有效任务。
                log.debug("Ignoring invalid kuva mission row: {}", invalidRow.getMessage());
            }
        }
        return List.copyOf(missions.values());
    }
}
