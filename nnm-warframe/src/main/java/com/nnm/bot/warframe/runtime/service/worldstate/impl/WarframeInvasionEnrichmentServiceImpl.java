package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeInvasionProperties;
import com.nnm.bot.warframe.runtime.constant.WarframeInvasionMissionType;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.InvasionDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.InvasionRewardDTO;
import com.nnm.bot.warframe.runtime.rawdata.invasion.OracleInvasionsRawData;
import com.nnm.bot.warframe.runtime.rawdata.invasion.OracleInvasionsRawData.Invasion;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeInvasionEnrichmentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 官方优先，只有同轮次、同入侵 ID、节点、阵营、奖励都匹配才采用任务类型。 */
@Slf4j
@Service
public class WarframeInvasionEnrichmentServiceImpl implements WarframeInvasionEnrichmentService {
    private static final int MAX_INVASIONS = 256;
    private static final int MAX_MISSIONS_PER_SIDE = 10;
    private static final long MAX_ROTATION_SECONDS = 3_600L;
    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeInvasionProperties properties;
    private ObjectReader inputReader;

    /** 在启动时创建严格协议读入器。 */
    @PostConstruct
    void initialize() {
        inputReader = WarframeInputBinding.strictMapper(objectMapper).readerFor(OracleInvasionsRawData.class);
    }

    /** @param official 官方入侵快照；@return 补充源失败时仍返回完整官方数据。 */
    @Override
    public WorldStateDTO enrich(WorldStateDTO official) {
        if (!properties.isEnabled() || official.invasions().isEmpty())
            return official;
        try {
            byte[] body =
                httpClient.getLimitedBytes(properties.getUrl().toString(), Map.of(), properties.getMaxBytes());
            OracleInvasionsRawData response = inputReader.readValue(body);
            Instant now = Instant.now();
            if (response == null || response.activation() == null || response.expiry() == null
                || response.invasions() == null || response.invasions().size() > MAX_INVASIONS
                || response.activation() > now.getEpochSecond() || response.expiry() <= now.getEpochSecond()
                || response.expiry() - response.activation() > MAX_ROTATION_SECONDS)
                return official;
            List<InvasionDTO> updated =
                official.invasions().stream().map(row -> merge(row, response.invasions())).toList();
            return updated.equals(official.invasions()) ? official : official.withInvasions(updated);
        } catch (IOException | RuntimeException exception) {
            // 补充任务不是官方主数据；禁止让社区源异常阻断官方快照发布。
            log.warn("Invasion mission source unavailable; retaining official invasion data", exception);
            return official;
        }
    }

    /** @param official 当前入侵；@param entries 同轮次的增量；@return 身份匹配后逐方补齐的入侵。 */
    private static InvasionDTO merge(InvasionDTO official, List<Invasion> entries) {
        if (official.id() == null || official.node() == null)
            return official;
        List<String> attack = new ArrayList<>();
        List<String> defense = new ArrayList<>();
        for (Invasion row : entries) {
            if (row == null || !official.id().equals(row.id()) || !official.node().equals(row.node())
                || row.missions() == null || row.missions().isEmpty() || row.missions().size() > MAX_MISSIONS_PER_SIDE
                || row.missions().stream().anyMatch(Objects::isNull))
                continue;
            if (official.attackerFaction() != null && official.attackerFaction().equals(row.ally())
                && sameRewards(official.attackerRewards(), row))
                attack.addAll(row.missions());
            if (official.defenderFaction() != null && official.defenderFaction().equals(row.ally())
                && sameRewards(official.defenderRewards(), row))
                defense.addAll(row.missions());
        }
        if (attack.isEmpty() && defense.isEmpty())
            return official;
        return official.withMissions(attack.stream().map(WarframeInvasionMissionType::officialCode).toList(),
            defense.stream().map(WarframeInvasionMissionType::officialCode).toList());
    }

    /** @param official 官方一方的原始奖励；@param supplemental 补充源一方；@return 奖励身份和数量均一致。 */
    private static boolean sameRewards(List<InvasionRewardDTO> official, Invasion supplemental) {
        if (supplemental.allyPay() == null)
            return false;
        Map<String, Long> left = official.stream().filter(item -> item.item() != null && item.count() != null)
            .collect(Collectors.groupingBy(item -> item.item() + "#" + item.count(), Collectors.counting()));
        Map<String,
            Long> right = supplemental.allyPay().stream().filter(Objects::nonNull)
                .filter(item -> item.ItemType() != null && item.ItemCount() != null).collect(
                    Collectors.groupingBy(item -> item.ItemType() + "#" + item.ItemCount(), Collectors.counting()));
        return !left.isEmpty() && left.equals(right);
    }
}
