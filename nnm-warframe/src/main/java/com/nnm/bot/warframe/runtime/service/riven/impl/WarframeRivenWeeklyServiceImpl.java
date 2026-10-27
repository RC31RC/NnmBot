package com.nnm.bot.warframe.runtime.service.riven.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenWeaponDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.riven.RivenWeeklyEntryDTO;
import java.io.IOException;
import com.nnm.bot.warframe.runtime.client.WarframeRuntimeDataClient.FetchedText;
import com.nnm.bot.warframe.runtime.config.riven.WarframeRivenWeeklyProperties.Source;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import java.util.Map;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.runtime.client.WarframeRuntimeDataClient;
import com.nnm.bot.warframe.runtime.config.riven.WarframeRivenWeeklyProperties;
import com.nnm.bot.warframe.persistent.official.constant.WarframeTranslationKey;
import com.nnm.bot.warframe.runtime.parser.riven.WarframeRivenWeeklyParser;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO.EntryVO;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenWeaponService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class WarframeRivenWeeklyServiceImpl implements WarframeRivenWeeklyService {
    private final ReentrantLock snapshotLock = new ReentrantLock();

    @Resource
    private WarframeRivenWeeklyProperties properties;
    @Resource
    private WarframeRuntimeDataClient client;
    @Resource
    private WarframeRivenWeeklyParser parser;
    @Resource
    private CacheManager cacheManager;
    @Resource
    private Clock warframeClock;
    @Resource
    private WarframeItemNameService itemNameService;
    @Resource
    private WarframeRivenWeaponService weaponCatalog;

    /**
     * 按给定业务条件查询周紫卡成交统计。
     *
     * @param platform 市场或官方接口使用的平台代码。
     * @param query 当前业务的查询条件。
     * @param rerolled 已洗练。
     * @param page 从一开始的页码。
     * @return 当前平台周紫卡成交统计的分页视图。
     */
    @Override
    public WarframeRivenWeeklyVO find(String platform, String query, Boolean rerolled, int page) {
        if (!properties.getSources().containsKey(platform) || page < 1) {
            throw new IllegalArgumentException("Invalid weekly Riven platform or page");
        }
        Snapshot snapshot = snapshot(platform);
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        Map<String,WarframeReferenceResolutionDTO> names = itemNameService.resolveAll(snapshot.entries().stream()
                .map(RivenWeeklyEntryDTO::compatibility).filter(Objects::nonNull).map(WarframeTranslationKey::name).distinct().toList());
        Map<String, WarframeRivenWeaponDTO> weapons = weaponCatalog.index();
        List<EntryVO> entries = snapshot.entries().stream().map(entry -> {
                    WarframeReferenceResolutionDTO translated = entry.compatibility() == null ? null
                            : names.get(WarframeTranslationKey.name(entry.compatibility()));
                    WarframeRivenWeaponDTO weapon = weapons.get(
                            WarframeNameNormalizer.weeklyWeapon(entry.compatibility()));
                    String name = weapon != null && StrUtil.isNotBlank(weapon.nameZh())
                            ? weapon.nameZh() : translated != null && translated.resolved()
                            ? translated.displayName() : entry.compatibility() == null ? entry.itemType() : entry.compatibility();
                    return new EntryVO(name, entry.compatibility(),
                            entry.itemType(), entry.rerolled(), entry.avg(), entry.median(), entry.min(), entry.max(),
                            entry.stddev(), entry.pop(), weapon == null ? null : weapon.uniqueName());
                }).filter(entry -> (rerolled == null || entry.rerolled() == rerolled)
                        && (StrUtil.isEmpty(keyword) || contains(entry.weaponName(), keyword)
                        || contains(entry.compatibility(), keyword) || contains(entry.itemType(), keyword)))
                .sorted(Comparator.comparing(EntryVO::weaponName).thenComparing(EntryVO::itemType)
                        .thenComparing(EntryVO::rerolled)).toList();
        int pages = Math.max(1, (int) ((entries.size() + 7L) / 8));
        int from = (int) Math.min((page - 1L) * 8, entries.size());
        boolean stale = snapshot.fetchedAt() != null
                && !warframeClock.instant().isBefore(snapshot.fetchedAt().plus(properties.getRefreshInterval()));
        String notice = snapshot.fetchedAt() == null ? "官方周成交统计暂不可用"
                : stale ? "来源刷新失败，当前为上一份成功快照"
                : CollUtil.isEmpty(entries) ? "没有匹配的周成交统计" : from == entries.size() ? "该页没有统计记录" : null;
        return new WarframeRivenWeeklyVO(query, platform, page + "/" + pages + " 页，共 "
                + entries.size() + " 条", properties.getSources().get(platform).getSourceCode(),
                snapshot.fetchedAt(), stale, entries.subList(from, Math.min(from + 8, entries.size())), notice);
    }

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    @Override
    public void refresh() {
        for (String platform : List.of("pc", "ps4", "xb1", "swi")) {
            snapshot(platform);
        }
    }

    /**
     * 取得当前业务的有效快照。
     *
     * @param platform 市场或官方接口使用的平台代码。
     * @return 快照对象。
     */
    private Snapshot snapshot(String platform) {
        snapshotLock.lock();
        try {
            Cache cache = Objects.requireNonNull(cacheManager.getCache(WarframeCacheNames.RIVEN_WEEKLY));
            Snapshot previous = cache.get(platform, Snapshot.class);
            Instant now = warframeClock.instant();
            if (previous != null && now.isBefore(previous.nextAttemptAt())) {
                return previous;
            }
            Snapshot next;
            try {
                Source source = properties.getSources().get(platform);
                FetchedText fetched = client.fetch(source.getSourceCode(), source.getUrls());
                next = new Snapshot(parser.parse(fetched.body(), properties.getMaxRecords()),
                        now, now.plus(properties.getRefreshInterval()));
            } catch (IOException | RuntimeException exception) {
                next = new Snapshot(previous == null ? List.of() : previous.entries(),
                        previous == null ? null : previous.fetchedAt(), now.plus(properties.getRetryInterval()));
                log.warn("官方紫卡周统计刷新失败，保留上一份快照, platform={}, message={}",
                        platform, WarframeRuntimeDataClient.failureMessage(exception));
            }
            cache.put(platform, next);
            return next;
        } finally {
            snapshotLock.unlock();
        }
    }

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param keyword 筛选使用的关键词。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private record Snapshot(List<RivenWeeklyEntryDTO> entries, Instant fetchedAt, Instant nextAttemptAt) {}
}
