package com.nnm.bot.warframe.runtime.service.steelincursion.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.runtime.cache.steelincursion.WarframeSteelIncursionSnapshotStore;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import com.nnm.bot.warframe.runtime.constant.WarframeRuntimeDataErrorCode;
import com.nnm.bot.warframe.runtime.pojo.dto.steelincursion.SteelIncursionEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.query.steelincursion.WarframeSteelIncursionQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.steelincursion.WarframeSteelIncursionManagementVO;
import com.nnm.bot.warframe.runtime.service.steelincursion.WarframeSteelIncursionService;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/** 只查询本实例已采集的钢铁侵袭快照。 */
@Service
public class WarframeSteelIncursionServiceImpl implements WarframeSteelIncursionService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    @Resource
    private WarframeSteelIncursionSnapshotStore snapshots;
    @Resource
    private WarframeSteelIncursionProperties properties;
    @Resource
    private Clock warframeClock;

    /** @param query 时间、关键词和分页条件。 @return 符合条件的独立钢铁侵袭快照。 */
    @Override
    public WarframeSteelIncursionManagementVO page(WarframeSteelIncursionQuery query) {
        int current = ObjUtil.defaultIfNull(query.current(), 1);
        int size = ObjUtil.defaultIfNull(query.size(), DEFAULT_PAGE_SIZE);
        BusinessAssert.isTrue(current > 0 && size > 0 && size <= MAX_PAGE_SIZE,
            WarframeRuntimeDataErrorCode.QUERY_INVALID);
        BusinessAssert.isTrue(query.from() == null || query.until() == null || query.from().isBefore(query.until()),
            WarframeRuntimeDataErrorCode.TIME_RANGE_INVALID);
        String keyword = CharSequenceUtil.trimToEmpty(query.keyword());
        var snapshot = snapshots.current();
        Instant now = warframeClock.instant();
        Instant defaultFrom = now.truncatedTo(ChronoUnit.DAYS);
        List<SteelIncursionEntryDTO> matches = snapshot.entries().stream()
            .filter(entry -> query.from() == null && query.until() == null
                ? !entry.activation().isBefore(defaultFrom)
                    && entry.activation().isBefore(defaultFrom.plus(1, ChronoUnit.DAYS))
                : (query.from() == null || entry.expiry().isAfter(query.from()))
                    && (query.until() == null || entry.activation().isBefore(query.until())))
            .filter(entry -> CharSequenceUtil.isEmpty(keyword)
                || Stream.of(entry.node(), entry.nodeReference(), entry.planet(), entry.missionType(), entry.enemy())
                    .anyMatch(value -> CharSequenceUtil.containsIgnoreCase(value, keyword)))
            .toList();
        int start = (int)Math.min((current - 1L) * size, matches.size());
        Instant fetchedAt = snapshot.fetchedAt();
        boolean stale = fetchedAt != null && !now.isBefore(fetchedAt.plus(properties.getRefreshInterval()));
        Instant rangeFrom = CollUtil.isEmpty(snapshot.entries()) ? null : snapshot.entries().getFirst().activation();
        Instant rangeUntil = CollUtil.isEmpty(snapshot.entries()) ? null : snapshot.entries().getLast().expiry();
        String notice = "browse.wf 每日钢铁侵袭节点；排期快照每 10 天刷新，任务按每日轮换展示";
        return new WarframeSteelIncursionManagementVO(fetchedAt != null, stale, properties.getUrl().toString(),
            fetchedAt, rangeFrom, rangeUntil, PageResult.of(
                matches.subList(start, Math.min(start + size, matches.size())), current, size, matches.size()),
            notice);
    }
}
