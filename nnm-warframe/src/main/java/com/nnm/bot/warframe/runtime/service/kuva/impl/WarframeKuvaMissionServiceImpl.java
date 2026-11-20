package com.nnm.bot.warframe.runtime.service.kuva.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.runtime.cache.kuva.WarframeKuvaMissionSnapshotStore;
import com.nnm.bot.warframe.runtime.client.kuva.WarframeKuvaMissionClient;
import com.nnm.bot.warframe.runtime.config.kuva.WarframeKuvaMissionProperties;
import com.nnm.bot.warframe.runtime.constant.WarframeRuntimeDataErrorCode;
import com.nnm.bot.warframe.runtime.parser.kuva.WarframeKuvaMissionParser;
import com.nnm.bot.warframe.runtime.pojo.dto.kuva.KuvaMissionDTO;
import com.nnm.bot.warframe.runtime.pojo.query.kuva.WarframeKuvaMissionQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.kuva.WarframeKuvaMissionManagementVO;
import com.nnm.bot.warframe.runtime.service.kuva.WarframeKuvaMissionService;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 编排赤毒任务采集，并从只读内存快照提供管理端查询。 */
@Slf4j
@Service
public class WarframeKuvaMissionServiceImpl implements WarframeKuvaMissionService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;
    @Resource
    private WarframeKuvaMissionClient client;
    @Resource
    private WarframeKuvaMissionParser parser;
    @Resource
    private WarframeKuvaMissionSnapshotStore snapshots;
    @Resource
    private WarframeKuvaMissionProperties properties;
    @Resource
    private Clock warframeClock;

    /** 获取并校验结构化任务流；失败不会清空上一份成功快照。 */
    @Override
    public void refresh() {
        try {
            List<KuvaMissionDTO> missions = parser.parse(client.fetch());
            snapshots.replace(missions, warframeClock.instant());
        } catch (IOException | RuntimeException exception) {
            log.warn("赤毒任务流刷新失败，保留最近一次有效快照 source={}", properties.getUrl(), exception);
        }
    }

    /** @param query 关键词和分页条件。 @return 过滤后的当前赤毒任务及采集元数据。 */
    @Override
    public WarframeKuvaMissionManagementVO page(WarframeKuvaMissionQuery query) {
        int current = ObjUtil.defaultIfNull(query.current(), 1);
        int size = ObjUtil.defaultIfNull(query.size(), DEFAULT_PAGE_SIZE);
        BusinessAssert.isTrue(current > 0 && size > 0 && size <= MAX_PAGE_SIZE,
            WarframeRuntimeDataErrorCode.QUERY_INVALID);
        String keyword = CharSequenceUtil.trimToEmpty(query.keyword());
        Instant now = warframeClock.instant();
        var snapshot = snapshots.current();
        List<KuvaMissionDTO> matches = snapshot.missions().stream().filter(mission -> mission.expiry().isAfter(now))
            .filter(mission -> CharSequenceUtil.isEmpty(keyword)
                || CharSequenceUtil.containsIgnoreCase(mission.node(), keyword)
                || CharSequenceUtil.containsIgnoreCase(mission.nodeReference(), keyword)
                || CharSequenceUtil.containsIgnoreCase(mission.missionType(), keyword))
            .toList();
        int start = (int)Math.min((current - 1L) * size, matches.size());
        Instant fetchedAt = snapshot.fetchedAt();
        boolean stale = fetchedAt != null && !now.isBefore(fetchedAt.plus(properties.getInterval()));
        String notice = fetchedAt == null ? "等待独立赤毒任务轮询首次成功" : "10o.io 结构化任务流；数据与官方世界状态分开采集和展示";
        return new WarframeKuvaMissionManagementVO(fetchedAt != null, stale, properties.getUrl().toString(), fetchedAt,
            PageResult.of(matches.subList(start, Math.min(start + size, matches.size())), current, size,
                matches.size()),
            notice);
    }

}
