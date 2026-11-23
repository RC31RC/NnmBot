package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.common.http.ExternalHttpException;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex.Reference;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex.Snapshot;
import com.nnm.bot.warframe.persistent.official.pojo.query.worldstate.WarframeBountyReferenceQuery;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeBountyCycleProperties;
import com.nnm.bot.warframe.runtime.client.worldstate.WarframeBountyCycleClient;
import com.nnm.bot.warframe.runtime.constant.WarframeBountyCycleConstants.Syndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeBountySupplementStatus;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.BountyCycleJobDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.BountyStaticPoolDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SyndicateMissionDTO;
import com.nnm.bot.warframe.runtime.rawdata.bounty.BountyCycleRawData;
import com.nnm.bot.warframe.runtime.rawdata.bounty.BountyCycleRawData.JobRawData;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeBountyCycleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** oracle 是补充动态源，官方 PHP 与已采纳静态包始终是判断轮次和解释引用的依据。 */
@Slf4j
@Service
public class WarframeBountyCycleServiceImpl implements WarframeBountyCycleService {
    private static final Snapshot EMPTY_REFERENCES = new Snapshot(Map.of(), Map.of(), Map.of());
    private static final String BOUNTY_POOL_KEY_PREFIX = "rewardPools/";
    @Resource
    private WarframeBountyCycleClient client;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeBountyCycleProperties properties;
    @Resource
    private WarframeOfficialWorldStateIndex staticLookup;
    /** 按外部友军完整路径读取人工核验的限定词条。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

    /** 仅影响补充赏金协议；禁止字符串、浮点数被静默转换成时间戳。 */
    private ObjectReader inputReader;

    /** 启动时创建严格绑定器，避免每次刷新复制 Jackson 配置。 */
    @PostConstruct
    void initialize() {
        inputReader = WarframeInputBinding.strictMapper(objectMapper).readerFor(BountyCycleRawData.class);
    }

    /**
     * 为支持的集团补全静态奖池，并仅合并与官方过期毫秒完全一致的补充赏金。
     *
     * @param state 已经解析的当前世界状态。
     * @return 保留官方字段并附加补充任务、静态奖池及解析状态的世界状态；无可用静态资料时返回原对象。
     */
    @Override
    public WorldStateDTO enrich(WorldStateDTO state) {
        if (state.syndicateMissions().stream().noneMatch(mission -> Syndicate.supports(mission.syndicate()))) {
            return state;
        }
        BountyCycleRawData cycle = null;
        WarframeBountySupplementStatus status = properties.isEnabled() ? WarframeBountySupplementStatus.UNAVAILABLE
            : WarframeBountySupplementStatus.DISABLED;
        if (properties.isEnabled()) {
            try {
                cycle = inputReader.readValue(client.fetch());
                if (cycle == null || cycle.getExpiry() == null || cycle.getBounties() == null) {
                    cycle = null;
                    status = WarframeBountySupplementStatus.INVALID_RESPONSE;
                }
            } catch (IOException exception) {
                status = WarframeBountySupplementStatus.INVALID_RESPONSE;
                log.warn("赏金补充数据与类型化输入合同不符", exception);
            } catch (ExternalHttpException exception) {
                log.warn("Bounty cycle source unavailable; retaining official missions (status={})",
                    exception.getStatusCode());
            } catch (RuntimeException exception) {
                // 补充源失败不能导致 PHP 轮询失败，也不能沿用上一轮社区任务冒充本轮。
                log.warn("Bounty cycle source unavailable; keeping official fields and static reward pools", exception);
            }
        }
        var query = referencesFor(state.syndicateMissions(), cycle, readPoolMappings());
        Snapshot index = lookupStatic(query);
        Map<String, List<String>> allyNames = query.nodes().isEmpty() ? Map.of() : readAllyNames();
        List<SyndicateMissionDTO> missions = new ArrayList<>();
        for (SyndicateMissionDTO mission : state.syndicateMissions()) {
            missions.add(
                Syndicate.supports(mission.syndicate()) ? merge(mission, cycle, status, index, allyNames) : mission);
        }
        return state.withSyndicateMissions(missions);
    }

    private Map<WarframeCommunityMappingScope, Map<String, List<String>>> readPoolMappings() {
        try {
            return communityCatalog.mappingTargets();
        } catch (RuntimeException exception) {
            log.warn("Bounty pool mappings unavailable; keeping official missions", exception);
            return Map.of();
        }
    }

    private Snapshot lookupStatic(WarframeBountyReferenceQuery query) {
        try {
            return staticLookup.current(query);
        } catch (RuntimeException exception) {
            log.warn("Bounty static references unavailable; retaining raw bounty-cycle jobs", exception);
            return EMPTY_REFERENCES;
        }
    }

    /**
     * 只把通过毫秒级轮次校验的动态节点/挑战和明确配置的候选奖池交给数据库查询。
     *
     * @param missions 官方集团任务。
     * @param cycle 外部增量，可能不可用。
     * @param mappings 社区候选奖池对应关系。
     * @return 同批查询需要的精确业务键。
     */
    private static WarframeBountyReferenceQuery referencesFor(List<SyndicateMissionDTO> missions,
        BountyCycleRawData cycle, Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings) {
        Set<String> nodes = new LinkedHashSet<>();
        Set<String> challenges = new LinkedHashSet<>();
        Map<String, String> pools = new LinkedHashMap<>();
        for (var mission : missions) {
            String group = mission.syndicate();
            if (!Syndicate.supports(group))
                continue;
            WarframeCommunityMappingScope.BOUNTY_POOL.target(mappings, group)
                .filter(key -> StrUtil.startWith(key, BOUNTY_POOL_KEY_PREFIX)).ifPresent(key -> pools.put(group, key));
            if (CollUtil.isNotEmpty(mission.jobs()) || !matchesRound(mission, cycle))
                continue;
            var rows = rowsFor(group, cycle);
            if (CollUtil.isNotEmpty(rows) && rows.stream().allMatch(WarframeBountyCycleServiceImpl::validJob)) {
                rows.forEach(row -> {
                    nodes.add(row.getNode());
                    challenges.add(row.getChallenge());
                });
            }
        }
        return new WarframeBountyReferenceQuery(nodes, challenges, pools);
    }

    private static boolean matchesRound(SyndicateMissionDTO mission, BountyCycleRawData cycle) {
        return cycle != null && mission.expiry() != null && cycle.getExpiry() == mission.expiry().toEpochMilli();
    }

    private static boolean validJob(JobRawData row) {
        return row != null && !StrUtil.hasBlank(row.getNode(), row.getChallenge());
    }

    private static List<JobRawData> rowsFor(String syndicate, BountyCycleRawData cycle) {
        return switch (Syndicate.fromCode(syndicate)) {
            case HEX -> cycle.getBounties().getHex();
            case ZARIMAN -> cycle.getBounties().getZariman();
            case ENTRATI_LAB -> cycle.getBounties().getEntratiLab();
            case null -> List.of();
        };
    }

    private Map<String, List<String>> readAllyNames() {
        try {
            return WarframeCommunityMappingScope.BOUNTY_ALLY.targets(communityCatalog.mappingTargets());
        } catch (RuntimeException exception) {
            log.warn("Bounty ally terms unavailable; preserving the source references", exception);
            return Map.of();
        }
    }

    /**
     * 按官方任务优先、轮次严格一致的规则组装单个集团的赏金信息。
     *
     * @param official 官方世界状态已经绑定的集团任务。
     * @param cycle 本次拉取并绑定的补充赏金；拉取失败或输入无效时为空。
     * @param fallbackStatus 补充源禁用、不可用或输入无效时的状态。
     * @param index 从已采纳静态资料建立的节点、挑战及奖池索引。
     * @param allyNames 本轮查库确认的友军名称。
     * @return 保留官方任务并携带本轮补充任务、静态奖池和状态的非受管 DTO。
     */
    private WorldStateDTO.SyndicateMissionDTO merge(SyndicateMissionDTO official, BountyCycleRawData cycle,
        WarframeBountySupplementStatus fallbackStatus, Snapshot index, Map<String, List<String>> allyNames) {
        List<BountyStaticPoolDTO> pools = index.pools().getOrDefault(official.syndicate(), List.of()).stream()
            .map(pool -> new BountyStaticPoolDTO(pool.id(), pool.name(), pool.minLevel(), pool.rewards())).toList();
        List<BountyCycleJobDTO> jobs = new ArrayList<>();
        WarframeBountySupplementStatus status = fallbackStatus;
        if (CollUtil.isNotEmpty(official.jobs())) {
            status = WarframeBountySupplementStatus.OFFICIAL_JOBS_PROVIDED;
        } else if (cycle != null) {
            // 官方 Mongo date 与 oracle expiry 都以毫秒表示，不能舍入到秒或只比较日期。
            if (!matchesRound(official, cycle)) {
                status = WarframeBountySupplementStatus.EXPIRY_MISMATCH_NOT_MERGED;
            } else {
                List<JobRawData> rows = rowsFor(official.syndicate(), cycle);
                status = CollUtil.isNotEmpty(rows) ? WarframeBountySupplementStatus.EXPIRY_MATCHES_OFFICIAL_PHP
                    : WarframeBountySupplementStatus.NO_SUPPLEMENTAL_JOBS;
                if (CollUtil.isNotEmpty(rows)) {
                    int tier = 0;
                    for (JobRawData row : rows) {
                        tier++;
                        if (!validJob(row)) {
                            jobs.clear();
                            status = WarframeBountySupplementStatus.INVALID_RESPONSE;
                            break;
                        }
                        String nodeKey = row.getNode();
                        String challengeKey = row.getChallenge();
                        String ally = row.getAlly();
                        List<String> names = allyNames.getOrDefault(WarframeNameNormalizer.phrase(ally), List.of());
                        String allyName = names.size() == 1 ? names.getFirst() : null;
                        Reference node = index.nodes().get(nodeKey);
                        Reference challenge = index.challenges().get(challengeKey);
                        jobs.add(new WorldStateDTO.BountyCycleJobDTO(nodeKey, node == null ? null : node.displayName(),
                            node == null ? null : node.systemName(), node == null ? null : node.missionType(),
                            challengeKey, challenge == null ? null : challenge.displayName(),
                            challenge == null ? null : challenge.descriptionZh(), ally, allyName,
                            node == null || challenge == null
                                ? WarframeWorldStateResolutionStatus.UNRESOLVED_STATIC_REFERENCE
                                : StrUtil.isBlank(ally) || StrUtil.isNotBlank(allyName)
                                    ? WarframeWorldStateResolutionStatus.RESOLVED
                                    : WarframeWorldStateResolutionStatus.ALLY_UNRESOLVED,
                            node == null ? null : node.missionTypeKey(), tier, rows.size()));
                    }
                }
            }
        }
        return new WorldStateDTO.SyndicateMissionDTO(official.id(), official.syndicate(), official.node(),
            official.activation(), official.expiry(), official.jobs(), official.nodes(), jobs, pools, status);
    }
}
