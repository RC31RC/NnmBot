package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.BountyCycleJobDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.BountyStaticPoolDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SyndicateMissionDTO;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex.Reference;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.warframe.runtime.rawdata.bounty.BountyCycleRawData;
import com.nnm.bot.warframe.runtime.rawdata.bounty.BountyCycleRawData.JobRawData;
import jakarta.annotation.PostConstruct;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeBountyCycleProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeBountyCycleService;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex.Snapshot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** oracle 是补充动态源，官方 PHP 与已采纳静态包始终是判断轮次和解释引用的依据。 */
@Slf4j
@Service
public class WarframeBountyCycleServiceImpl implements WarframeBountyCycleService {
    private static final Set<String> SUPPORTED = Set.of("HexSyndicate", "ZarimanSyndicate", "EntratiLabSyndicate");

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeBountyCycleProperties properties;
    @Resource
    private WarframeOfficialWorldStateIndex staticLookup;

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
        if (state.syndicateMissions().stream().noneMatch(mission -> mission.syndicate() != null && SUPPORTED.contains(mission.syndicate()))) {
            return state;
        }
        Snapshot index;
        try {
            index = staticLookup.current();
        } catch (RuntimeException exception) {
            log.warn("Bounty static index unavailable; official WorldStateDTO remains usable", exception);
            return state;
        }
        if (!index.available()) return state;
        BountyCycleRawData cycle = null;
        String status = properties.isEnabled() ? "unavailable" : "disabled";
        if (properties.isEnabled()) {
            try {
                byte[] body = httpClient.getLimitedBytes(properties.getUrl().toString(), Map.of(), properties.getMaxBytes());
                cycle = inputReader.readValue(body);
                if (cycle == null || cycle.getExpiry() == null || cycle.getBounties() == null) {
                    cycle = null;
                    status = "invalid_response";
                }
            } catch (IOException exception) {
                status = "invalid_response";
                log.warn("赏金补充数据与类型化输入合同不符", exception);
            } catch (RuntimeException exception) {
                // 补充源失败不能导致 PHP 轮询失败，也不能沿用上一轮社区任务冒充本轮。
                log.warn("Bounty cycle source unavailable; keeping official fields and static reward pools", exception);
            }
        }
        List<SyndicateMissionDTO> missions = new ArrayList<>();
        for (SyndicateMissionDTO mission : state.syndicateMissions()) {
            missions.add(mission.syndicate() != null && SUPPORTED.contains(mission.syndicate()) ? merge(mission, cycle, status, index) : mission);
        }
        return state.withSyndicateMissions(missions);
    }

    /**
     * 按官方任务优先、轮次严格一致的规则组装单个集团的赏金信息。
     *
     * @param official 官方世界状态已经绑定的集团任务。
     * @param cycle 本次拉取并绑定的补充赏金；拉取失败或输入无效时为空。
     * @param fallbackStatus 补充源禁用、不可用或输入无效时的状态。
     * @param index 从已采纳静态资料建立的节点、挑战及奖池索引。
     * @return 保留官方任务并携带本轮补充任务、静态奖池和状态的非受管 DTO。
     */
    private WorldStateDTO.SyndicateMissionDTO merge(SyndicateMissionDTO official, BountyCycleRawData cycle,
                                              String fallbackStatus, Snapshot index) {
        List<BountyStaticPoolDTO> pools = index.pools().getOrDefault(official.syndicate(), List.of())
                .stream().map(pool -> new WorldStateDTO.BountyStaticPoolDTO(pool.id(), pool.name(), pool.rewards())).toList();
        List<BountyCycleJobDTO> jobs = new ArrayList<>();
        String status = fallbackStatus;
        if (CollUtil.isNotEmpty(official.jobs())) {
            status = "official_jobs_provided";
        } else if (cycle != null) {
            // 官方 Mongo date 与 oracle expiry 都以毫秒表示，不能舍入到秒或只比较日期。
            if (official.expiry() == null || cycle.getExpiry() != official.expiry().toEpochMilli()) {
                status = "expiry_mismatch_not_merged";
            } else {
                List<JobRawData> rows = switch (official.syndicate()) {
                    case "HexSyndicate" -> cycle.getBounties().getHex();
                    case "ZarimanSyndicate" -> cycle.getBounties().getZariman();
                    case "EntratiLabSyndicate" -> cycle.getBounties().getEntratiLab();
                    default -> List.of();
                };
                status = CollUtil.isNotEmpty(rows) ? "expiry_matches_official_php" : "no_supplemental_jobs";
                if (CollUtil.isNotEmpty(rows)) {
                    int tier = 0;
                    for (JobRawData row : rows) {
                        tier++;
                        if (row == null || row.getNode() == null || row.getChallenge() == null) {
                            jobs.clear();
                            status = "invalid_response";
                            break;
                        }
                        String nodeKey = row.getNode();
                        String challengeKey = row.getChallenge();
                        String ally = row.getAlly();
                        Reference node = index.nodes().get(nodeKey);
                        Reference challenge = index.challenges().get(challengeKey);
                        // 成品尚未发布 ally agent 身份映射时只保留原始键，禁止从 tmp 复制名字表或猜测。
                        jobs.add(new WorldStateDTO.BountyCycleJobDTO(nodeKey, node == null ? null : node.displayName(),
                                node == null ? null : node.systemName(), node == null ? null : node.missionType(),
                                challengeKey, challenge == null ? null : challenge.displayName(),
                                challenge == null ? null : challenge.descriptionZh(), ally, null,
                                node == null || challenge == null ? "unresolved_static_reference"
                                        : ally == null ? "resolved" : "ally_unresolved",
                                node == null ? null : node.missionTypeKey(), tier, rows.size()));
                    }
                }
            }
        }
        return new WorldStateDTO.SyndicateMissionDTO(official.id(), official.syndicate(), official.node(),
                official.activation(), official.expiry(), official.jobs(), official.nodes(), jobs, pools, status);
    }
}
