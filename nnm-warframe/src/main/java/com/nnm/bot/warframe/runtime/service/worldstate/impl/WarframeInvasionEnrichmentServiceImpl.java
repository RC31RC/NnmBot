package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.common.http.ExternalHttpException;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeInvasionProperties;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.runtime.client.worldstate.WarframeInvasionClient;
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
    private WarframeInvasionClient client;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeInvasionProperties properties;
    /** Oracle 简称和官方任务代码的受管对应关系。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalog;
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
            OracleInvasionsRawData response = inputReader.readValue(client.fetch());
            Instant now = Instant.now();
            if (response == null || response.activation() == null || response.expiry() == null
                || response.invasions() == null || response.invasions().size() > MAX_INVASIONS
                || response.activation() > now.getEpochSecond() || response.expiry() <= now.getEpochSecond()
                || response.expiry() - response.activation() > MAX_ROTATION_SECONDS)
                return official;
            var mappings = missionMappings();
            List<InvasionDTO> updated =
                official.invasions().stream().map(row -> merge(row, response.invasions(), mappings)).toList();
            return updated.equals(official.invasions()) ? official : official.withInvasions(updated);
        } catch (ExternalHttpException exception) {
            // HTTP 客户端已经记录每次失败及底层原因，这里只记录本轮的降级结果。
            log.warn("Invasion mission source unavailable; retaining official invasion data (status={})",
                exception.getStatusCode());
            return official;
        } catch (IOException | RuntimeException exception) {
            // 补充任务不是官方主数据；禁止让社区源异常阻断官方快照发布。
            log.warn("Invasion mission source unavailable; retaining official invasion data", exception);
            return official;
        }
    }

    private Map<WarframeCommunityMappingScope, Map<String, List<String>>> missionMappings() {
        try {
            return communityCatalog.mappingTargets();
        } catch (RuntimeException exception) {
            log.warn("Invasion mission mappings unavailable; retaining the matched source codes", exception);
            return Map.of();
        }
    }

    /**
     * 对齐官方入侵及补充增量，匹配成功后才应用受管任务简称。
     *
     * @param official 当前入侵。
     * @param entries 同轮次的增量。
     * @param mappings 本轮查询的简称索引。
     * @return 身份匹配后逐方补齐的入侵。
     */
    private static InvasionDTO merge(InvasionDTO official, List<Invasion> entries, Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings) {
        if (StrUtil.hasBlank(official.id(), official.node()))
            return official;
        List<String> attack = new ArrayList<>();
        List<String> defense = new ArrayList<>();
        for (Invasion row : entries) {
            if (row == null || !StrUtil.equals(official.id(), row.id()) || !StrUtil.equals(official.node(), row.node())
                || CollUtil.isEmpty(row.missions()) || row.missions().size() > MAX_MISSIONS_PER_SIDE
                || row.missions().stream().anyMatch(Objects::isNull))
                continue;
            if (StrUtil.isNotBlank(row.ally()) && StrUtil.equals(official.attackerFaction(), row.ally())
                && sameRewards(official.attackerRewards(), row))
                attack.addAll(row.missions());
            if (StrUtil.isNotBlank(row.ally()) && StrUtil.equals(official.defenderFaction(), row.ally())
                && sameRewards(official.defenderRewards(), row))
                defense.addAll(row.missions());
        }
        if (attack.isEmpty() && defense.isEmpty())
            return official;
        return official.withMissions(attack.stream().map(code -> missionCode(mappings, code)).toList(),
            defense.stream().map(code -> missionCode(mappings, code)).toList());
    }

    private static String missionCode(Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings, String code) {
        return WarframeCommunityMappingScope.MISSION_TYPE.target(mappings, code).filter(target -> target.startsWith("MT_"))
            .orElse(code);
    }

    /** @param official 官方一方的原始奖励；@param supplemental 补充源一方；@return 奖励身份和数量均一致。 */
    private static boolean sameRewards(List<InvasionRewardDTO> official, Invasion supplemental) {
        if (supplemental.allyPay() == null)
            return false;
        Map<RewardIdentity, Long> left =
            official.stream().filter(item -> StrUtil.isNotBlank(item.item()) && item.count() != null).collect(
                Collectors.groupingBy(item -> new RewardIdentity(item.item(), item.count()), Collectors.counting()));
        Map<RewardIdentity,
            Long> right = supplemental.allyPay().stream().filter(Objects::nonNull)
                .filter(item -> StrUtil.isNotBlank(item.ItemType()) && item.ItemCount() != null).collect(Collectors
                    .groupingBy(item -> new RewardIdentity(item.ItemType(), item.ItemCount()), Collectors.counting()));
        return !left.isEmpty() && left.equals(right);
    }

    private record RewardIdentity(String item, int count) {
    }
}
