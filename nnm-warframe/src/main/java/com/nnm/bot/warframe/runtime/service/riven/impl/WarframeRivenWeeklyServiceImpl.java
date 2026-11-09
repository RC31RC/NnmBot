package com.nnm.bot.warframe.runtime.service.riven.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.runtime.pojo.query.riven.WarframeRivenWeeklyQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyManagementVO;
import static com.nnm.bot.warframe.runtime.constant.WarframeRuntimeQueryConstants.*;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
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
     * @param conditions 平台、武器、洗练状态及分页条件。
     * @return 当前平台周紫卡成交统计的分页视图。
     */
    @Override
    public WarframeRivenWeeklyVO find(WarframeRivenWeeklyQuery conditions) {
        String platform = conditions.platform() == null ? "pc" : conditions.platform();
        String query = conditions.keyword();
        Boolean rerolled = conditions.rerolled();
        int page = conditions.current() == null ? 1 : conditions.current();
        int pageSize = conditions.size() == null ? DEFAULT_PAGE_SIZE : conditions.size();
        BusinessAssert.isTrue(pageSize > 0 && pageSize <= MAX_PAGE_SIZE, CommonErrorCode.PARAM_INVALID);
        if (!properties.getSources().containsKey(platform) || page < 1) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "无效的裂罅周统计平台或页码");
        }
        Snapshot snapshot = snapshot(platform);
        List<EntryVO> entries = entries(snapshot, query, rerolled);
        int pages = Math.max(1, (int)((entries.size() + pageSize - 1L) / pageSize));
        int from = (int)Math.min((page - 1L) * pageSize, entries.size());
        boolean stale = snapshot.fetchedAt() != null
            && !warframeClock.instant().isBefore(snapshot.fetchedAt().plus(properties.getRefreshInterval()));
        String notice = snapshot.fetchedAt() == null ? "官方周成交统计暂不可用" : stale ? "来源刷新失败，当前为上一份成功快照"
            : CollUtil.isEmpty(entries) ? "没有匹配的周成交统计" : from == entries.size() ? "该页没有统计记录" : null;
        return new WarframeRivenWeeklyVO(query, platform, page + "/" + pages + " 页，共 " + entries.size() + " 条",
            properties.getSources().get(platform).getSourceCode(), snapshot.fetchedAt(), stale,
            entries.subList(from, Math.min(from + pageSize, entries.size())), notice);
    }

    /**
     * 查询已有内存快照；空快照只返回等待状态，不在 HTTP 请求中下载。
     *
     * @param query 平台、武器、洗练状态和分页条件。
     * @return 采集状态及查询分页。
     */
    @Override
    public WarframeRivenWeeklyManagementVO page(WarframeRivenWeeklyQuery query) {
        String platform = query.platform() == null ? "pc" : query.platform();
        int current = query.current() == null ? 1 : query.current();
        int size = query.size() == null ? DEFAULT_PAGE_SIZE : query.size();
        BusinessAssert.isTrue(
            properties.getSources().containsKey(platform) && current > 0 && size > 0 && size <= MAX_PAGE_SIZE,
            CommonErrorCode.PARAM_INVALID);
        Cache cache = Objects.requireNonNull(cacheManager.getCache(WarframeCacheNames.RIVEN_WEEKLY));
        Snapshot snapshot = cache.get(platform, Snapshot.class);
        if (snapshot == null) {
            snapshot = new Snapshot(List.of(), null, Instant.MIN);
        }
        List<EntryVO> entries = entries(snapshot, query.keyword(), query.rerolled());
        int start = (int)Math.min((current - 1L) * size, entries.size());
        boolean stale = snapshot.fetchedAt() != null
            && !warframeClock.instant().isBefore(snapshot.fetchedAt().plus(properties.getRefreshInterval()));
        return new WarframeRivenWeeklyManagementVO(snapshot.fetchedAt() != null, stale,
            properties.getSources().get(platform).getSourceCode(), platform, snapshot.fetchedAt(),
            PageResult.of(entries.subList(start, Math.min(start + size, entries.size())), current, size,
                entries.size()),
            snapshot.fetchedAt() == null ? "尚未完成采集，请等待后台任务拉取" : stale ? "当前为最近一次成功快照，后台正在按周期重试" : "官方周成交统计，价格单位为白金");
    }

    /**
     * 复用静态名称补全和筛选，命令与管理端共享相同业务解释。
     *
     * @param snapshot 最近一次成功快照。
     * @param query 名称关键词。
     * @param rerolled 洗练状态，空值表示全部。
     * @return 稳定排序后的展示行。
     */
    private List<EntryVO> entries(Snapshot snapshot, String query, Boolean rerolled) {
        if (snapshot.entries().isEmpty()) {
            return List.of();
        }
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        Map<String, WarframeReferenceResolutionDTO> names =
            itemNameService.resolveAll(snapshot.entries().stream().map(RivenWeeklyEntryDTO::compatibility)
                .filter(Objects::nonNull).map(WarframeTranslationKey::name).distinct().toList());
        Map<String, WarframeRivenWeaponDTO> weapons = weaponCatalog.index();
        return snapshot.entries().stream().map(entry -> {
            WarframeReferenceResolutionDTO translated =
                entry.compatibility() == null ? null : names.get(WarframeTranslationKey.name(entry.compatibility()));
            WarframeRivenWeaponDTO weapon = weapons.get(WarframeNameNormalizer.weeklyWeapon(entry.compatibility()));
            String name = weapon != null && StrUtil.isNotBlank(weapon.nameZh()) ? weapon.nameZh()
                : translated != null && translated.resolved() ? translated.displayName()
                    : entry.compatibility() == null ? entry.itemType() : entry.compatibility();
            return new EntryVO(name, entry.compatibility(), entry.itemType(), entry.rerolled(), entry.avg(),
                entry.median(), entry.min(), entry.max(), entry.stddev(), entry.pop(),
                weapon == null ? null : weapon.uniqueName());
        }).filter(entry -> (rerolled == null || entry.rerolled() == rerolled)
            && (StrUtil.isEmpty(keyword) || contains(entry.weaponName(), keyword)
                || contains(entry.compatibility(), keyword) || contains(entry.itemType(), keyword)))
            .sorted(Comparator.comparing(EntryVO::weaponName).thenComparing(EntryVO::itemType)
                .thenComparing(EntryVO::rerolled))
            .toList();
    }

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    @Override
    public void refresh() {
        for (String platform : properties.getSources().keySet()) {
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
                next = new Snapshot(parser.parse(fetched.body(), properties.getMaxRecords()), now,
                    now.plus(properties.getRefreshInterval()));
            } catch (IOException | RuntimeException exception) {
                next = new Snapshot(previous == null ? List.of() : previous.entries(),
                    previous == null ? null : previous.fetchedAt(), now.plus(properties.getRetryInterval()));
                log.warn("官方紫卡周统计刷新失败，保留上一份快照, platform={}, message={}", platform,
                    WarframeRuntimeDataClient.failureMessage(exception));
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

    private record Snapshot(List<RivenWeeklyEntryDTO> entries, Instant fetchedAt, Instant nextAttemptAt) {
    }
}
