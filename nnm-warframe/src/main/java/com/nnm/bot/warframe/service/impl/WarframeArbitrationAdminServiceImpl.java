package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.application.WarframeArbitrationAdminService;
import com.nnm.bot.warframe.application.WarframeArbitrationStore;
import com.nnm.bot.warframe.client.WarframeArbitrationClient;
import com.nnm.bot.warframe.config.properties.WarframeArbitrationProperties;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.nnm.bot.warframe.constant.WarframeCommunityPreviewStatus;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;
import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMergeReceipt;
import com.nnm.bot.warframe.parser.ArbitrationScheduleParser;
import com.nnm.bot.warframe.pojo.WarframeArbitrationMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeArbitrationPreview;
import com.nnm.bot.warframe.pojo.WarframeArbitrationPreviewRecord;
import com.nnm.bot.warframe.pojo.WarframeArbitrationPreviewSnapshot;
import com.nnm.bot.warframe.repository.WarframeArbitrationScheduleRepository;
import com.nnm.bot.warframe.service.WarframeCommunityDatasetLock;
import io.github.rdt.cache.CacheEntry;
import io.github.rdt.cache.CacheService;
import io.github.rdt.lock.LockHandle;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarframeArbitrationAdminServiceImpl implements WarframeArbitrationAdminService {

    private static final String PREVIEW_CACHE_PREFIX = "warframe-arbitration-preview:";
    private static final String PENDING_PREVIEW_CACHE_KEY =
            "warframe-arbitration-preview-pending";
    private static final String CONSUMED_PREVIEW_CACHE_PREFIX =
            "warframe-arbitration-preview-consumed:";
    private static final String DATASET_LOCK_KEY = "arbitration_schedule";

    @Resource
    private WarframeArbitrationClient client;
    @Resource
    private ArbitrationScheduleParser parser;
    @Resource
    private WarframeArbitrationProperties arbitrationProperties;
    @Resource
    private WarframeCommunityDataProperties communityProperties;
    @Resource
    private WarframeArbitrationScheduleRepository repository;
    @Resource
    private WarframeArbitrationStore store;
    @Resource
    private CacheService cacheService;
    @Resource
    private Clock warframeClock;
    @Resource
    private WarframeCommunityDatasetLock datasetLock;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public WarframeArbitrationPreview refreshPreview(TaskProgressReporter reporter) {
        LockHandle lock = datasetLock.acquire(DATASET_LOCK_KEY);
        try {
            CacheEntry<String> pending = cacheService.get(
                    PENDING_PREVIEW_CACHE_KEY, String.class);
            if (pending instanceof CacheEntry.Hit<?> hit
                    && hit.value() instanceof String previewId
                    && !previewId.isBlank()) {
                if (isConsumed(previewId)) {
                    cacheService.evict(PENDING_PREVIEW_CACHE_KEY);
                } else {
                    try {
                        return preview(previewId);
                    } catch (BusinessException exception) {
                        if (exception.getCode()
                                != WarframeManagementErrorCode.ARBITRATION_PREVIEW_NOT_FOUND.getCode()) {
                            throw exception;
                        }
                        cacheService.evict(PENDING_PREVIEW_CACHE_KEY);
                    }
                }
            }
            TaskProgressReporter progress = reporter == null
                    ? TaskProgressReporter.noop() : reporter;
            progress.report("FETCHING", "正在下载仲裁排期", 1, 3);
            String payload = client.fetch();
            progress.report("PARSING", "正在解析仲裁排期", 2, 3);
            List<ArbitrationScheduleEntry> upstream = parser.parse(payload);
            if (upstream.isEmpty()) {
                throw new BusinessException(
                        WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                        "仲裁排期解析结果为空");
            }
            Instant from = upstream.stream()
                    .map(ArbitrationScheduleEntry::activation)
                    .min(Instant::compareTo)
                    .orElseThrow();
            Instant to = upstream.stream()
                    .map(ArbitrationScheduleEntry::expiry)
                    .max(Instant::compareTo)
                    .orElseThrow();
            List<WarframeArbitrationSchedule> current = repository
                    .findByExpiryGreaterThanAndActivationLessThanOrderByActivationAsc(from, to);
            WarframeArbitrationPreviewSnapshot snapshot = createSnapshot(
                    upstream, current, arbitrationProperties.getUrl().toString());
            cacheService.put(previewCacheKey(snapshot.previewId()), snapshot,
                    communityProperties.getPreviewRetention());
            cacheService.put(PENDING_PREVIEW_CACHE_KEY, snapshot.previewId(),
                    communityProperties.getPreviewRetention());
            progress.complete("仲裁排期预览生成完成");
            return new WarframeArbitrationPreview(snapshot);
        } finally {
            lock.close();
        }
    }

    @Override
    public WarframeArbitrationPreview preview(String previewId) {
        return new WarframeArbitrationPreview(loadPreview(previewId));
    }

    @Override
    @Transactional
    public WarframeArbitrationPreview merge(WarframeArbitrationMergeRequest request) {
        if (request == null || request.selectedOperationIds() == null) {
            throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID);
        }
        LockHandle lock = datasetLock.acquire(DATASET_LOCK_KEY);
        try {
            if (isConsumed(request.previewId())) {
                throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                        "仲裁预览已经合并，不能重复提交");
            }
            WarframeArbitrationPreviewSnapshot snapshot = loadPreview(request.previewId());
            Set<String> selected = request.selectedOperationIds().stream()
                    .filter(id -> id != null && !id.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toSet());
            Map<String, WarframeArbitrationPreviewRecord> records = snapshot.records().stream()
                    .collect(LinkedHashMap::new,
                            (map, record) -> map.put(record.operationId(), record),
                            Map::putAll);
            if (!records.keySet().containsAll(selected)
                    || selected.size() != request.selectedOperationIds().size()) {
                    throw new BusinessException(
                            WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                            "提交包含不属于该预览的操作 ID");
            }
            List<ArbitrationScheduleEntry> applied = new ArrayList<>();
            List<Instant> deleted = new ArrayList<>();
            for (String operationId : selected) {
                WarframeArbitrationPreviewRecord record = records.get(operationId);
                ArbitrationScheduleEntry current = repository.findById(record.activation())
                        .map(WarframeArbitrationAdminServiceImpl::toEntry).orElse(null);
                if (!Objects.equals(current, record.current())) {
                    throw new BusinessException(
                            WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                            "仲裁排期已变化，请重新刷新");
                }
                switch (record.changeType()) {
                    case ADDED, CHANGED -> {
                        if (record.upstream() == null) {
                            throw new BusinessException(
                                    WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                                    "不能应用不存在的上游仲裁记录");
                        }
                        applied.add(record.upstream());
                    }
                    case REMOVED -> {
                        if (record.current() == null || record.upstreamPresent()) {
                            throw new BusinessException(
                                    WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                                    "只能删除已从上游消失的仲裁记录");
                        }
                        deleted.add(record.activation());
                    }
                    default -> throw new BusinessException(
                            WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                            "仲裁预览包含非差异操作");
                }
            }
            if (!applied.isEmpty() || !deleted.isEmpty()) {
                store.merge(applied, deleted, snapshot.source(), warframeClock.instant());
            }
            var receipt = new WarframeCommunityMergeReceipt();
            receipt.setPreviewId(snapshot.previewId());
            receipt.setDataset(DATASET_LOCK_KEY);
            receipt.setCommittedAt(warframeClock.instant());
            entityManager.persist(receipt);
            consumeAfterCommit(snapshot);
            return new WarframeArbitrationPreview(snapshot);
        } finally {
            datasetLock.releaseAfterTransaction(lock);
        }
    }

    private WarframeArbitrationPreviewSnapshot createSnapshot(
            List<ArbitrationScheduleEntry> upstream,
            List<WarframeArbitrationSchedule> current,
            String source) {
        Map<Instant, ArbitrationScheduleEntry> upstreamByActivation = upstream.stream()
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.activation(), entry),
                        Map::putAll);
        Map<Instant, ArbitrationScheduleEntry> currentByActivation = current.stream()
                .collect(LinkedHashMap::new,
                        (map, record) -> map.put(record.getActivation(), toEntry(record)),
                        Map::putAll);
        List<Instant> activations = new ArrayList<>(upstreamByActivation.keySet());
        currentByActivation.keySet().stream()
                .filter(activation -> !upstreamByActivation.containsKey(activation))
                .forEach(activations::add);
        activations.sort(Comparator.naturalOrder());

        int added = 0;
        int changed = 0;
        int removed = 0;
        List<WarframeArbitrationPreviewRecord> records = new ArrayList<>();
        for (Instant activation : activations) {
            ArbitrationScheduleEntry upstreamEntry = upstreamByActivation.get(activation);
            ArbitrationScheduleEntry currentEntry = currentByActivation.get(activation);
            WarframeCommunityRecordChangeType changeType;
            if (currentEntry == null) {
                added++;
                changeType = WarframeCommunityRecordChangeType.ADDED;
            } else if (upstreamEntry == null) {
                removed++;
                changeType = WarframeCommunityRecordChangeType.REMOVED;
            } else if (currentEntry.equals(upstreamEntry)) {
                changeType = WarframeCommunityRecordChangeType.UNCHANGED;
            } else {
                changed++;
                changeType = WarframeCommunityRecordChangeType.CHANGED;
            }
            if (changeType == WarframeCommunityRecordChangeType.UNCHANGED) {
                continue;
            }
            records.add(new WarframeArbitrationPreviewRecord(
                    UUID.randomUUID().toString(),
                    activation, currentEntry, upstreamEntry, upstreamEntry != null,
                    changeType));
        }
        Instant createdAt = warframeClock.instant();
        return new WarframeArbitrationPreviewSnapshot(
                UUID.randomUUID().toString(),
                createdAt,
                createdAt.plus(communityProperties.getPreviewRetention()),
                source,
                records.size(),
                added,
                changed,
                removed,
                List.copyOf(records),
                WarframeCommunityPreviewStatus.PENDING);
    }

    private WarframeArbitrationPreviewSnapshot loadPreview(String previewId) {
        if (previewId == null || previewId.isBlank()) {
            throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_NOT_FOUND);
        }
        if (isConsumed(previewId)) {
            throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                    "仲裁预览已经合并，不能重复查看");
        }
        CacheEntry<WarframeArbitrationPreviewSnapshot> entry = cacheService.get(
                previewCacheKey(previewId.trim()), WarframeArbitrationPreviewSnapshot.class);
        if (entry instanceof CacheEntry.Hit<WarframeArbitrationPreviewSnapshot> hit
                && hit.value() != null
                && hit.value().expiresAt().isAfter(warframeClock.instant())) {
            if (hit.value().status() != WarframeCommunityPreviewStatus.PENDING) {
                throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_INVALID,
                        "仲裁预览已经合并，不能重复查看");
            }
            return hit.value();
        }
        throw new BusinessException(WarframeManagementErrorCode.ARBITRATION_PREVIEW_NOT_FOUND);
    }

    private boolean isConsumed(String previewId) {
        if (previewId == null || previewId.isBlank()) {
            return false;
        }
        if (entityManager.find(WarframeCommunityMergeReceipt.class, previewId.trim()) != null) {
            compensateCleanup(previewId.trim());
            return true;
        }
        CacheEntry<String> entry = cacheService.get(
                consumedPreviewCacheKey(previewId.trim()), String.class);
        boolean consumed = entry instanceof CacheEntry.Hit<?> hit
                && hit.value() instanceof String value
                && !value.isBlank();
        if (consumed) {
            compensateCleanup(previewId.trim());
        }
        return consumed;
    }

    private void consumeAfterCommit(WarframeArbitrationPreviewSnapshot snapshot) {
        Runnable consume = () -> {
            try {
                cacheService.put(consumedPreviewCacheKey(snapshot.previewId()),
                        snapshot.source(), communityProperties.getPreviewRetention());
            } catch (RuntimeException ignored) {
                // 继续尝试把预览自身标记为已合并，避免消费标记短暂写入失败导致重放。
            }
            try {
                cacheService.put(previewCacheKey(snapshot.previewId()), snapshot.merged(),
                        communityProperties.getPreviewRetention());
            } catch (RuntimeException ignored) {
                // 数据库幂等回执已经随业务提交，Redis 全部不可用也不能重放。
            }
            try {
                cacheService.evict(PENDING_PREVIEW_CACHE_KEY);
                compensateCleanup(snapshot.previewId());
            } catch (RuntimeException ignored) {
                // 已消费状态会让后续读取继续补偿清理。
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            consume.run();
                        }
                    });
        } else {
            consume.run();
        }
    }

    private void compensateCleanup(String previewId) {
        try {
            // 旧批次的重复请求不能清除后来新建的待确认批次索引。
            cacheService.evict(previewCacheKey(previewId));
        } catch (RuntimeException ignored) {
            // 消费标记已经阻止重放，后续读取入口继续补偿清理。
        }
    }

    private static String consumedPreviewCacheKey(String previewId) {
        return CONSUMED_PREVIEW_CACHE_PREFIX + previewId;
    }

    private static ArbitrationScheduleEntry toEntry(WarframeArbitrationSchedule record) {
        return new ArbitrationScheduleEntry(
                record.getUpstreamId(),
                record.getActivation(),
                record.getExpiry(),
                record.getNode(),
                record.getMissionType(),
                record.getEnemy(),
                record.getPlanet(),
                record.getBoundsJson());
    }

    private static String previewCacheKey(String previewId) {
        return PREVIEW_CACHE_PREFIX + previewId;
    }
}
