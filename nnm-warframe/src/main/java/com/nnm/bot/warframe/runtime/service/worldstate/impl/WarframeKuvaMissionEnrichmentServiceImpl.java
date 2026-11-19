package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeKuvaMissionProperties;
import com.nnm.bot.warframe.runtime.constant.WarframeInvasionMissionType;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.KuvaMissionDTO;
import com.nnm.bot.warframe.runtime.rawdata.kuva.KuvaLogRawData;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeKuvaMissionEnrichmentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 外部日志只补当前小时赤毒任务；仲裁仍由既有的 browse.wf 排期源负责。 */
@Slf4j
@Service
public class WarframeKuvaMissionEnrichmentServiceImpl implements WarframeKuvaMissionEnrichmentService {
    private static final int MAX_LOG_ENTRIES = 50_000;
    private static final Duration START_BUFFER = Duration.ofMinutes(5);
    private static final Duration END_BUFFER = Duration.ofMinutes(1);
    private static final Duration MAX_MISSION_DURATION = Duration.ofHours(2);
    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeKuvaMissionProperties properties;
    private ObjectReader inputReader;

    /** 创建严格但忽略已声明扩展字段的外部协议读取器。 */
    @PostConstruct
    void initialize() {
        inputReader =
            WarframeInputBinding.strictMapper(objectMapper).readerFor(new TypeReference<List<KuvaLogRawData>>() {});
    }

    /** @param official 官方世界状态；@return 从可用结构化日志提取的任务，失败时不影响官方快照。 */
    @Override
    public WorldStateDTO enrich(WorldStateDTO official) {
        if (!properties.isEnabled())
            return official;
        try {
            byte[] body =
                httpClient.getLimitedBytes(properties.getUrl().toString(), Map.of(), properties.getMaxBytes());
            List<KuvaLogRawData> rows = inputReader.readValue(body);
            if (rows == null || rows.size() > MAX_LOG_ENTRIES)
                return official;
            Instant now = Instant.now();
            Map<String, KuvaMissionDTO> missions = new LinkedHashMap<>();
            for (KuvaLogRawData row : rows) {
                if (row == null || row.missiontype() == null
                    || !row.missiontype().startsWith(WarframeWorldStateDisplayConstants.KUVA_MISSION_PREFIX)
                    || row.solnode() == null || row.solnodedata() == null || row.solnodedata().type() == null)
                    continue;
                try {
                    Instant start = Instant.parse(row.start());
                    Instant end = Instant.parse(row.end());
                    // 日志以整点开始、65 分结束；参照 WFCD 过滤边缘缓冲，防止旧轮次错挂。
                    Instant activation = start.plus(START_BUFFER);
                    Instant expiry = end.minus(END_BUFFER);
                    if (now.isBefore(activation) || !now.isBefore(expiry)
                        || expiry.isAfter(activation.plus(MAX_MISSION_DURATION)))
                        continue;
                    missions.putIfAbsent(row.solnode(), new KuvaMissionDTO(row.solnode(),
                        WarframeInvasionMissionType.officialCode(row.solnodedata().type()), activation, expiry));
                } catch (RuntimeException invalidRow) {
                    // 部分任务时间错误时，只丢弃该行，不能影响同批有效日志。
                    log.debug("Ignoring invalid kuva mission row: {}", invalidRow.getMessage());
                }
            }
            return official.withKuvaMissions(List.copyOf(missions.values()));
        } catch (IOException | RuntimeException exception) {
            log.warn("Kuva mission stream unavailable; retaining official world state: {}", exception.getMessage());
            return official;
        }
    }
}
