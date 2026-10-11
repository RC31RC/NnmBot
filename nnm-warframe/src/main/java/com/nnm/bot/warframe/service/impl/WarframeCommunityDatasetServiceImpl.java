package com.nnm.bot.warframe.service.impl;

import jakarta.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetProjectionMapper;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetService;
import com.nnm.bot.warframe.application.WarframeDatasetRecordIdentity;
import com.nnm.bot.warframe.cache.WarframeItemIndexInvalidator;
import com.nnm.bot.warframe.cache.WarframeCommunityPreviewStore;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties.DatasetSource;
import com.nnm.bot.warframe.config.properties.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunityPreviewStatus;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.model.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityWorldStateRule;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateRecord;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateLanguage;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMergeReceipt;
import com.nnm.bot.warframe.parser.ArbitrationEssenceParser;
import com.nnm.bot.warframe.parser.ArbitrationScheduleParser;
import com.nnm.bot.warframe.application.WarframeArbitrationStore;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;
import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import com.nnm.bot.warframe.repository.WarframeArbitrationScheduleRepository;
import com.nnm.bot.warframe.parser.ExportRewardsParser;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateSortie;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetBatchRefreshResult;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewPage;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewChangeGroup;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewGroup;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewIndex;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewSnapshot;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewSnapshotRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetRefreshSummary;
import com.nnm.bot.warframe.pojo.WarframeCommunityFieldDiff;
import com.nnm.bot.warframe.pojo.WarframeCommunityGenericDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunitySourceSummary;
import com.nnm.bot.warframe.service.WarframeCommunitySourceLoader;
import com.nnm.bot.warframe.service.WarframeCommunitySourceLoader.LoadedSource;
import com.nnm.bot.warframe.service.WarframeCommunityDatasetLock;
import io.github.rdt.id.DistributedIdGenerator;
import io.github.rdt.lock.LockHandle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 社区仓库数据集服务。
 *
 * <p>刷新只生成 Redis 差异预览；管理员明确提交的记录才在事务内写入数据库。</p>
 */
@Service
@Slf4j
public class WarframeCommunityDatasetServiceImpl implements WarframeCommunityDatasetService {

    private static final long MAX_IMPORT_FILE_BYTES = 8L * 1024 * 1024;
    private static final int MAX_PAGE_SIZE = 200;
    private static final String PREVIEW_OPERATION_ID_SEQUENCE =
            "warframe-community-preview-operation-v1";

    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeCommunityDataProperties apiProperties;
    @Resource
    private WarframeWorldStateDataProperties worldStateProperties;
    @Resource
    private WarframeCommunitySourceLoader sourceLoader;
    @Resource
    private WarframeCommunityDatasetProjectionMapper projectionMapper;
    @Resource
    private WarframeItemIndexInvalidator itemIndexInvalidator;
    @Resource
    private WarframeCommunityPreviewStore previewStore;
    @Resource
    private WarframeCommunityDatasetLock datasetLock;
    @Resource
    private ArbitrationScheduleParser arbitrationScheduleParser;
    @Resource
    private WarframeArbitrationStore arbitrationStore;
    @Resource
    private WarframeArbitrationScheduleRepository arbitrationScheduleRepository;
    @Autowired(required = false)
    private DistributedIdGenerator ids;

    @Override
    public List<WarframeCommunityDatasetInfo> datasets() {
        return List.of(WarframeCommunityDataset.values()).stream()
                .map(dataset -> {
                    List<WarframeCommunityDatasetRecord> records = dataset
                            == WarframeCommunityDataset.ARBITRATION_SCHEDULE
                            ? arbitrationScheduleRepository.findAllByOrderByActivationAsc().stream()
                                    .map(this::arbitrationPreviewRecord)
                                    .toList()
                            : all(dataset);
                    WarframeCommunityDataset.Metadata metadata = dataset.metadata();
                    return new WarframeCommunityDatasetInfo(
                            dataset.code(), dataset.fileName(), dataset.label(),
                            records.size(), records.stream()
                                    .filter(WarframeCommunityDatasetRecord::isEnabled).count(),
                            metadata.tableName(), metadata.sourceType(),
                            WarframeCommunitySource.sourceCodes(dataset.code()),
                    records.stream()
                                    .map(WarframeCommunityDatasetRecord::getLastImportedAt)
                                    .filter(Objects::nonNull)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null),
                            WarframeCommunitySource.descriptionText(dataset.code()),
                            metadata.description(), metadata.consumerCommands(),
                            hasRefreshSource(dataset) && metadata.refreshable(),
                            metadata.manageable(), metadata.readOnly());
                })
                .toList();
    }

    @Override
    public PageResult<WarframeCommunityDatasetItem> query(
            String datasetCode, WarframeCommunityDatasetQueryRequest request) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        if (dataset == WarframeCommunityDataset.ARBITRATION_SCHEDULE) {
            return queryArbitrationSchedule(request);
        }
        String keyword = request == null ? null : request.keyword();
        int current = request == null || request.current() == null
                ? 1 : Math.max(1, request.current());
        int size = request == null || request.size() == null
                ? 20 : Math.min(MAX_PAGE_SIZE, Math.max(1, request.size()));
        List<WarframeCommunityDatasetItem> matched = all(dataset).stream()
                .filter(record -> matches(record, keyword))
                .sorted(Comparator.comparing(WarframeCommunityDatasetRecord::getRecordKey,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(record -> toItem(dataset, record))
                .toList();
        int from = (int) Math.min((long) (current - 1) * size, matched.size());
        int to = Math.min(from + size, matched.size());
        return PageResult.of(matched.subList(from, to), current, size, matched.size());
    }

    private PageResult<WarframeCommunityDatasetItem> queryArbitrationSchedule(
            WarframeCommunityDatasetQueryRequest request) {
        String keyword = request == null ? null : request.keyword();
        int current = request == null || request.current() == null
                ? 1 : Math.max(1, request.current());
        int size = request == null || request.size() == null
                ? 20 : Math.min(MAX_PAGE_SIZE, Math.max(1, request.size()));
        List<WarframeCommunityDatasetItem> matched =
                arbitrationScheduleRepository.findAllByOrderByActivationAsc().stream()
                        .map(this::arbitrationItem)
                        .filter(item -> StrUtil.isBlank(keyword)
                                || item.recordKey().toLowerCase(Locale.ROOT)
                                .contains(keyword.trim().toLowerCase(Locale.ROOT))
                                || item.currentJson().toLowerCase(Locale.ROOT)
                                .contains(keyword.trim().toLowerCase(Locale.ROOT)))
                        .toList();
        int from = (int) Math.min((long) (current - 1) * size, matched.size());
        int to = Math.min(from + size, matched.size());
        return PageResult.of(matched.subList(from, to), current, size, matched.size());
    }

    private WarframeCommunityDatasetItem arbitrationItem(WarframeArbitrationSchedule record) {
        JsonNode json = arbitrationJson(toEntry(record));
        return new WarframeCommunityDatasetItem(
                null, null, WarframeCommunityDataset.ARBITRATION_SCHEDULE.code(),
                record.getActivation().toString(),
                WarframeCommunityRecordSource.COMMUNITY, "browse.wf:arbys.txt",
                record.getFetchedAt() == null ? null
                        : LocalDateTime.ofInstant(record.getFetchedAt(), ZoneOffset.UTC),
                new WarframeCommunityGenericDetails(
                        Map.of("activation", record.getActivation().toString(),
                                "expiry", record.getExpiry().toString(),
                                "node", record.getNode(),
                                "missionType", record.getMissionType(),
                                "enemy", record.getEnemy(),
                                "planet", record.getPlanet())),
                writeJson(json), writeJson(json), writeJson(json), "{}",
                false, true, true, null, null, null);
    }

    @Override
    public WarframeCommunityDatasetPreview refresh(String datasetCode) {
        return refresh(datasetCode, TaskProgressReporter.noop());
    }

    @Override
    public WarframeCommunityDatasetPreview refresh(
            String datasetCode, TaskProgressReporter reporter) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        WarframeCommunityDataset lockDataset = aggregateRoot(dataset);
        LockHandle lock = datasetLock.acquire(lockDataset.code());
        try {
            return refreshLocked(lockDataset, reporter);
        } finally {
            lock.close();
        }
    }

    private WarframeCommunityDatasetPreview refreshLocked(
            WarframeCommunityDataset dataset, TaskProgressReporter reporter) {
        if (dataset == WarframeCommunityDataset.RELIC) {
            return refreshRelicAggregateLocked(reporter);
        }
        if (dataset == WarframeCommunityDataset.ARBITRATION_SCHEDULE) {
            return refreshArbitrationLocked(reporter);
        }
        WarframeCommunityDatasetPreview pending = pendingPreview(dataset);
        if (pending != null) {
            return pending;
        }
        BusinessAssert.isTrue(dataset.metadata().refreshable(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                dataset.label() + "仅支持管理端上传预览并人工合并，不支持远程刷新");
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        List<DatasetSource> sources = configuredSources(dataset);
        if (sources.isEmpty()) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "数据集未配置可用的远程来源：" + dataset.label());
        }
        return refreshSources(dataset, sources, progress);
    }

    private WarframeCommunityDatasetPreview refreshArbitrationLocked(
            TaskProgressReporter reporter) {
        WarframeCommunityDatasetPreview pending = pendingPreview(
                WarframeCommunityDataset.ARBITRATION_SCHEDULE);
        if (pending != null) {
            return pending;
        }
        List<DatasetSource> sources = configuredSources(WarframeCommunityDataset.ARBITRATION_SCHEDULE);
        BusinessAssert.isTrue(sources.size() == 1,
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "仲裁排期必须配置唯一来源");
        DatasetSource sourceDefinition = sources.getFirst();
        String sourceCode = sourceDefinition.getSourceCode();
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        List<LoadedSource<List<ArbitrationScheduleEntry>>> loaded = sourceLoader.load(
                WarframeCommunityDataset.ARBITRATION_SCHEDULE, sources,
                (definition, body) -> {
                    List<ArbitrationScheduleEntry> entries = arbitrationScheduleParser.parse(body);
                    BusinessAssert.isTrue(!entries.isEmpty(),
                            WarframeManagementErrorCode.INVALID_IMPORT_FILE, "仲裁排期解析结果为空");
                    return entries;
                }, List::size, progress);
        LoadedSource<List<ArbitrationScheduleEntry>> result = loaded.getFirst();
        if (!result.successful()) {
            WarframeCommunityDatasetPreview preview = savePreview(
                    WarframeCommunityDataset.ARBITRATION_SCHEDULE, sourceCode,
                    0, 0, 0, 0, List.of(), List.of(), Set.of(), List.of(result.summary()));
            completeRefresh(WarframeCommunityDataset.ARBITRATION_SCHEDULE, loaded, progress);
            return preview;
        }
        List<ArbitrationScheduleEntry> upstream = result.value();
        Instant from = upstream.stream().map(ArbitrationScheduleEntry::activation)
                .min(Instant::compareTo).orElseThrow();
        Instant to = upstream.stream().map(ArbitrationScheduleEntry::expiry)
                .max(Instant::compareTo).orElseThrow();
        // The upstream schedule only describes this window, not all stored history or future rows.
        List<WarframeArbitrationSchedule> current =
                arbitrationScheduleRepository
                        .findByExpiryGreaterThanAndActivationLessThanOrderByActivationAsc(from, to);
        Map<Instant, ArbitrationScheduleEntry> currentByActivation = current.stream()
                .collect(Collectors.toMap(WarframeArbitrationSchedule::getActivation,
                        WarframeCommunityDatasetServiceImpl::toEntry,
                        (first, ignored) -> first, LinkedHashMap::new));
        Map<Instant, ArbitrationScheduleEntry> upstreamByActivation = upstream.stream()
                .collect(Collectors.toMap(ArbitrationScheduleEntry::activation,
                        entry -> entry, (first, ignored) -> first, LinkedHashMap::new));
        List<WarframeCommunityDatasetItem> operations = new ArrayList<>();
        int added = 0;
        int changed = 0;
        List<Instant> activations = new ArrayList<>(upstreamByActivation.keySet());
        currentByActivation.keySet().stream()
                .filter(key -> !upstreamByActivation.containsKey(key)).forEach(activations::add);
        activations.sort(Instant::compareTo);
        for (Instant activation : activations) {
            ArbitrationScheduleEntry before = currentByActivation.get(activation);
            ArbitrationScheduleEntry after = upstreamByActivation.get(activation);
            WarframeCommunityRecordChangeType type;
            if (before == null) {
                type = WarframeCommunityRecordChangeType.ADDED;
                added++;
            } else if (after == null) {
                type = WarframeCommunityRecordChangeType.REMOVED;
            } else if (before.equals(after)) {
                continue;
            } else {
                type = WarframeCommunityRecordChangeType.CHANGED;
                changed++;
            }
            String beforeJson = before == null ? "{}" : writeJson(arbitrationJson(before));
            String afterJson = after == null ? "{}" : writeJson(arbitrationJson(after));
            String operationId = before == null
                    ? Long.toString(nextRecordId())
                    : activation.toString();
            operations.add(new WarframeCommunityDatasetItem(
                    null, operationId, WarframeCommunityDataset.ARBITRATION_SCHEDULE.code(),
                    activation.toString(), activation.toString(), null,
                    WarframeCommunityRecordSource.COMMUNITY, sourceCode, LocalDateTime.now(),
                    null, afterJson, afterJson, beforeJson, "{}", false,
                    after != null, after != null, type, null, null));
        }
        WarframeCommunityDatasetPreview preview = savePreview(
                WarframeCommunityDataset.ARBITRATION_SCHEDULE, sourceCode,
                upstream.size(), added, changed, 0, List.of(), operations, Set.of(sourceCode),
                List.of(result.summary()));
        completeRefresh(WarframeCommunityDataset.ARBITRATION_SCHEDULE, loaded, progress);
        return preview;
    }

    private JsonNode arbitrationJson(ArbitrationScheduleEntry entry) {
        ObjectNode value = objectMapper.createObjectNode();
        value.put("id", entry.id());
        value.put("activation", entry.activation().toString());
        value.put("expiry", entry.expiry().toString());
        value.put("node", entry.node());
        value.put("missionType", entry.missionType());
        value.put("enemy", entry.enemy());
        value.put("planet", entry.planet());
        try {
            value.set("bounds", objectMapper.readTree(entry.boundsJson()));
        } catch (JsonProcessingException exception) {
            throw invalidFile("仲裁排期 bounds 不是合法 JSON");
        }
        return value;
    }

    private static ArbitrationScheduleEntry toEntry(WarframeArbitrationSchedule record) {
        return new ArbitrationScheduleEntry(record.getUpstreamId(), record.getActivation(),
                record.getExpiry(), record.getNode(), record.getMissionType(), record.getEnemy(),
                record.getPlanet(), record.getBoundsJson());
    }

    private WarframeCommunityDatasetPreview refreshSources(
            WarframeCommunityDataset dataset,
            List<DatasetSource> sources,
            TaskProgressReporter progress) {
        List<LoadedSource<List<JsonNode>>> loaded = sourceLoader.load(
                dataset, sources,
                (definition, body) -> validateSourceRecords(dataset, definition.getSourceCode(),
                        parseRecords(dataset, body, definition.getWorld(),
                                definition.getSourceCode())),
                List::size, progress);
        List<SourceRecord> records = loaded.stream().filter(LoadedSource::successful)
                .flatMap(result -> result.value().stream()
                        .map(value -> new SourceRecord(result.summary().sourceCode(), value)))
                .toList();
        Set<String> refreshedSources = loaded.stream().filter(LoadedSource::successful)
                .map(result -> result.summary().sourceCode())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        progress.report("RECONCILING", "正在合并" + dataset.label(), 0, 1);
        String sourceText = loaded.stream().map(result -> result.summary().sourceCode())
                .collect(Collectors.joining(","));
        WarframeCommunityDatasetPreview preview =
                reconcileSourceRecords(dataset, sourceText, records, refreshedSources,
                        loaded.stream().map(LoadedSource::summary).toList());
        completeRefresh(dataset, loaded, progress);
        return preview;
    }

    private static void completeRefresh(
            WarframeCommunityDataset dataset,
            List<? extends LoadedSource<?>> loaded,
            TaskProgressReporter progress) {
        long failed = loaded.stream().filter(result -> !result.successful()).count();
        progress.complete(dataset.label() + (failed == loaded.size() ? "所有来源失败" : "刷新完成")
                + (failed == 0 ? "" : "，" + failed + " 个来源失败并保留原数据"));
    }

    /**
     * Relics.json 同时承载遗物主记录、奖励和掉落位置。以 relic 作为聚合入口，
     * 保证同一来源文件只下载一次，并让三个目标表共享一个预览和提交事务。
     */
    private WarframeCommunityDatasetPreview refreshRelicAggregateLocked(
            TaskProgressReporter reporter) {
        WarframeCommunityDatasetPreview pending = pendingPreview(WarframeCommunityDataset.RELIC);
        if (pending != null) {
            return pending;
        }
        List<DatasetSource> sources = configuredRelicSources();
        BusinessAssert.isTrue(!sources.isEmpty(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "遗物数据集未配置可用的远程来源");
        Map<WarframeCommunityDataset, List<SourceRecord>> records = new LinkedHashMap<>();
        Map<WarframeCommunityDataset, Set<String>> refreshedSources = new LinkedHashMap<>();
        for (WarframeCommunityDataset target : relicDatasets()) {
            records.put(target, new ArrayList<>());
            refreshedSources.put(target, new LinkedHashSet<>());
        }
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        List<LoadedSource<Map<WarframeCommunityDataset, List<JsonNode>>>> loaded =
                sourceLoader.load(WarframeCommunityDataset.RELIC, sources,
                        (definition, body) -> parseRelicBundle(body, definition.getSourceCode()),
                        bundle -> bundle.values().stream().mapToInt(List::size).sum(), progress);
        for (var result : loaded) {
            if (!result.successful()) {
                continue;
            }
            String sourceCode = result.summary().sourceCode();
            for (WarframeCommunityDataset target : relicDatasets()) {
                refreshedSources.get(target).add(sourceCode);
                List<JsonNode> targetRecords = result.value().getOrDefault(target, List.of());
                targetRecords.forEach(value ->
                        records.get(target).add(new SourceRecord(sourceCode, value)));
            }
        }
        progress.report("RECONCILING", "正在合并遗物数据", 0, 1);
        String sourceText = loaded.stream().map(result -> result.summary().sourceCode())
                .collect(Collectors.joining(","));
        WarframeCommunityDatasetPreview preview = reconcileRelicSourceRecords(
                sourceText, records, refreshedSources,
                loaded.stream().map(LoadedSource::summary).toList());
        completeRefresh(WarframeCommunityDataset.RELIC, loaded, progress);
        return preview;
    }

    @Override
    public WarframeCommunityDatasetBatchRefreshResult refreshAll(
            TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        List<WarframeCommunityDataset> datasets = Arrays.stream(
                        WarframeCommunityDataset.values())
                .filter(dataset -> dataset.metadata().refreshable()
                        && this.hasRefreshSource(dataset))
                .map(WarframeCommunityDatasetServiceImpl::aggregateRoot)
                .distinct()
                .toList();
        List<WarframeCommunityDatasetRefreshSummary> summaries =
                new ArrayList<>(datasets.size());
        for (int index = 0; index < datasets.size(); index++) {
            WarframeCommunityDataset dataset = datasets.get(index);
            progress.report(
                    "DATASET",
                    "正在刷新" + dataset.label(),
                    index,
                    datasets.size());
            // 批量刷新必须持续复用任务 reporter；否则单个数据集长时间下载时，
            // Redis 任务没有状态更新，会在固定期限后被误判为超时。
            WarframeCommunityDatasetPreview preview =
                    refresh(dataset.code(), progress);
            summaries.add(new WarframeCommunityDatasetRefreshSummary(
                    preview.previewId(),
                    dataset.code(),
                    dataset.label(),
                    preview.recordCount(),
                    preview.addedCount(),
                    preview.changedCount(),
                    preview.conflictCount()));
            progress.report(
                    "DATASET",
                    dataset.label() + "刷新完成",
                    index + 1,
                    datasets.size());
        }
        return new WarframeCommunityDatasetBatchRefreshResult(
                datasets.size(), summaries.size(), summaries);
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetItem createSelfMaintained(
            String datasetCode, String currentJson) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        assertManualManagementAllowed(dataset);
        LockHandle lock = datasetLock.acquire(aggregateRoot(dataset).code());
        try {
            ObjectNode json = readObject(currentJson);
            validateDatasetJson(dataset, json);
            String key = dataset == WarframeCommunityDataset.MARKET_RIVEN_TION_ALIAS
                    ? "alias:" + normalizeKey(text(json, "cn")) : recordKey(dataset, json);
            String identityKey = dataset == WarframeCommunityDataset.WORLDSTATE_SORTIE
                    ? key + "\u0000" + json.path("recordType").asText() : key;
            String normalizedKey = normalizeKey(WarframeDatasetRecordIdentity.of(
                    sourceCode(dataset, WarframeCommunityRecordSource.SELF_MAINTAINED),
                    dataset.code(), identityKey, locale(json)));
            boolean exists = all(dataset).stream()
                    .filter(WarframeCommunityDatasetServiceImpl::isSelfMaintained)
                    .anyMatch(record -> normalizeKey(identity(dataset, record)).equals(normalizedKey));
            if (exists) {
                throw new BusinessException(WarframeManagementErrorCode.SOURCE_KEY_DUPLICATED);
            }
            WarframeCommunityDatasetRecord record = newRecord(
                    dataset, key, json, WarframeCommunityRecordSource.SELF_MAINTAINED);
            record.setId(nextRecordId());
            entityManager.persist(record);
            itemIndexInvalidator.invalidateAfterCommit();
            return toItem(dataset, record);
        } finally {
            datasetLock.releaseAfterTransaction(lock);
        }
    }

    @Override
    public WarframeCommunityDatasetPreview uploadPreview(
            String datasetCode, MultipartFile file) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        assertManualManagementAllowed(dataset);
        BusinessAssert.isTrue(WarframeCommunitySource.forDataset(dataset.code()).size() == 1,
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "多来源数据集请使用主动刷新；上传入口尚不支持指定来源");
        return uploadPreview(dataset, WarframeCommunitySource.defaultCode(dataset.code()), file);
    }

    @Override
    public WarframeCommunityDatasetPreview uploadPreview(
            String datasetCode, String sourceCode, MultipartFile file) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        assertManualManagementAllowed(dataset);
        WarframeCommunitySource source = WarframeCommunitySource.parse(dataset.code(), sourceCode);
        return uploadPreview(dataset, source.code(), file);
    }

    private WarframeCommunityDatasetPreview uploadPreview(
            WarframeCommunityDataset dataset, String sourceCode, MultipartFile file) {
        LockHandle lock = datasetLock.acquire(aggregateRoot(dataset).code());
        try {
            WarframeCommunityDatasetPreview pending = pendingPreview(dataset);
            if (pending != null) {
                return pending;
            }
            String json = readFile(file, dataset.fileName());
            String sourceLabel = sourceCode + "|upload://"
                    + StrUtil.blankToDefault(file.getOriginalFilename(), dataset.fileName());
            if (aggregateRoot(dataset) == WarframeCommunityDataset.RELIC) {
                var bundle = parseRelicBundle(json, sourceCode);
                Map<WarframeCommunityDataset, List<SourceRecord>> records = new LinkedHashMap<>();
                Map<WarframeCommunityDataset, Set<String>> sources = new LinkedHashMap<>();
                bundle.forEach((target, values) -> {
                    records.put(target, values.stream().map(value -> new SourceRecord(sourceCode, value)).toList());
                    sources.put(target, Set.of(sourceCode));
                });
                return reconcileRelicSourceRecords(sourceLabel, records, sources);
            }
            return reconcile(dataset, sourceLabel, parseRecords(dataset, json, null, sourceCode));
        } finally {
            lock.close();
        }
    }

    @Override
    public WarframeCommunityDatasetPreview preview(String datasetCode, String previewId) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        WarframeCommunityDatasetPreviewSnapshot snapshot = datasetPreview(dataset, previewId);
        List<WarframeCommunityDatasetPreviewSnapshotRecord> snapshotRecords =
                previewRecords(dataset, snapshot);
        List<WarframeCommunityDatasetItem> records = snapshotRecords.stream()
                .map(record -> snapshotItem(dataset, record))
                .toList();
        return previewResponse(snapshot.previewId(), snapshot.createdAt(), snapshot.expiresAt(),
                dataset, snapshot.source(), snapshotRecords.size(),
                count(snapshotRecords, WarframeCommunityRecordChangeType.ADDED),
                count(snapshotRecords, WarframeCommunityRecordChangeType.CHANGED),
                snapshot.conflictCount(), snapshot.diffs(), records,
                sourceSummaries(dataset, snapshot, snapshotRecords));
    }

    @Override
    public WarframeCommunityDatasetPreviewPage previewPage(
            String datasetCode, String previewId, int current, int size,
            String sourceCode, WarframeCommunityRecordChangeType changeType) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        WarframeCommunityDatasetPreviewSnapshot header = datasetPreviewHeader(dataset, previewId);
        int page = Math.max(1, current);
        int limit = Math.clamp(size, 1, 100);
        BusinessAssert.isTrue(changeType == null || changeType.isOperation(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE, "预览仅支持新增、修改和删除操作");
        String source = StrUtil.trimToNull(sourceCode);
        boolean filtered = source != null || changeType != null;
        WarframeCommunityDatasetPreviewIndex index = previewStore.index(header);
        Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset =
                index.groupsByDataset();
        boolean paged = isPagedPreviewIndexAvailable(dataset, header, groupsByDataset)
                && (!filtered || !index.recordPositions().isEmpty());
        WarframeCommunityDatasetPreviewSnapshot assembled =
                paged ? header : previewStore.assemble(header);
        List<WarframeCommunityDatasetPreviewSnapshotRecord> allRecords =
                paged ? null : previewRecords(dataset, assembled);
        List<WarframeCommunityDatasetPreviewGroup> groups =
                previewGroups(dataset, assembled, groupsByDataset);
        List<WarframeCommunityDatasetPreviewSnapshotRecord> snapshotRecords;
        int total;
        if (paged && filtered) {
            List<String> operationIds = groups.stream()
                    .filter(group -> source == null || source.equals(group.sourceCode()))
                    .flatMap(group -> group.changeGroups().stream())
                    .filter(group -> changeType == null || changeType == group.changeType())
                    .flatMap(group -> group.operationIds().stream())
                    .toList();
            total = operationIds.size();
            List<Integer> positions = pageRecords(operationIds, page, limit).stream()
                    .map(index.recordPositions()::get).toList();
            snapshotRecords = previewStore.recordsAtPositions(header, positions);
        } else if (paged) {
            total = previewRecordCount(header);
            int from = (int) Math.min((long) (page - 1) * limit, total);
            snapshotRecords = previewStore.recordsInRange(header, from, Math.min(from + limit, total));
        } else {
            List<WarframeCommunityDatasetPreviewSnapshotRecord> matching = allRecords.stream()
                    .filter(record -> source == null || source.equals(
                            StrUtil.blankToDefault(record.sourceCode(), "unknown")))
                    .filter(record -> changeType == null || changeType == record.changeType())
                    .toList();
            total = matching.size();
            snapshotRecords = pageRecords(matching, page, limit);
        }
        List<WarframeCommunityDatasetItem> records = snapshotRecords.stream()
                .map(record -> snapshotItem(dataset, record))
                .toList();
        int removed = groups.stream()
                .flatMap(group -> group.changeGroups().stream())
                .filter(group -> group.changeType() == WarframeCommunityRecordChangeType.REMOVED)
                .mapToInt(group -> group.operationIds().size())
                .sum();
        return new WarframeCommunityDatasetPreviewPage(
                header.previewId(), header.createdAt(), header.expiresAt(),
                dataset.code(), header.source(), header.recordCount(),
                header.addedCount(), header.changedCount(), removed,
                PageResult.of(records, page, limit, total), groups,
                dataset == aggregateRoot(dataset)
                        ? header.sources()
                        : sourceSummaries(dataset, header, previewRecords(dataset, assembled)));
    }

    private List<WarframeCommunitySourceSummary> sourceSummaries(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetPreviewSnapshot header,
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records) {
        if (dataset == aggregateRoot(dataset)) {
            return header.sources();
        }
        // 子表分页使用过滤后的操作序号，不能沿用遗物聚合预览的跳转位置。
        return withSourceOperations(header.sources(),
                records.stream().map(WarframeCommunityDatasetPreviewSnapshotRecord::sourceCode)
                        .toList());
    }

    private static List<WarframeCommunitySourceSummary> withSourceOperations(
            List<WarframeCommunitySourceSummary> summaries, List<String> operationSources) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Integer> firstIndices = new HashMap<>();
        for (int index = 0; index < operationSources.size(); index++) {
            String source = operationSources.get(index);
            counts.merge(source, 1, Integer::sum);
            firstIndices.putIfAbsent(source, index);
        }
        return summaries.stream()
                .map(summary -> summary.withOperations(
                        counts.getOrDefault(summary.sourceCode(), 0),
                        firstIndices.get(summary.sourceCode())))
                .toList();
    }

    private WarframeCommunityDatasetPreviewSnapshot datasetPreview(
            WarframeCommunityDataset dataset, String previewId) {
        WarframeCommunityDatasetPreviewSnapshot snapshot = datasetPreviewHeader(dataset, previewId);
        return previewStore.assemble(snapshot);
    }

    private WarframeCommunityDatasetPreviewSnapshot datasetPreviewHeader(
            WarframeCommunityDataset dataset, String previewId) {
        WarframeCommunityDataset lockDataset = aggregateRoot(dataset);
        BusinessAssert.isTrue(!isPreviewConsumed(previewId, lockDataset.code()),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_ALREADY_MERGED);
        WarframeCommunityDatasetPreviewSnapshot snapshot = previewStore.header(previewId);
        BusinessAssert.isTrue(lockDataset.code().equals(snapshot.dataset()),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                "社区数据预览与当前数据集不匹配");
        return snapshot;
    }

    private static <T> List<T> pageRecords(
            List<T> records,
            int page,
            int limit) {
        int from = (int) Math.min((long) (page - 1) * limit, records.size());
        int to = Math.min(from + limit, records.size());
        return records.subList(from, to);
    }

    private boolean isPagedPreviewIndexAvailable(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetPreviewSnapshot header,
            Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset) {
        return (dataset == WarframeCommunityDataset.RELIC
                || aggregateRoot(dataset) != WarframeCommunityDataset.RELIC)
                && header.chunkCount() > 0
                && header.recordChunkSizes() != null
                && header.recordChunkSizes().size() == header.chunkCount()
                && groupsByDataset.containsKey(dataset.code());
    }

    private int previewRecordCount(WarframeCommunityDatasetPreviewSnapshot header) {
        return header.recordChunkSizes().stream().mapToInt(Integer::intValue).sum();
    }

    private List<WarframeCommunityDatasetPreviewGroup> previewGroups(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetPreviewSnapshot header,
            Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset) {
        List<WarframeCommunityDatasetPreviewGroup> groups = groupsByDataset.get(dataset.code());
        if (groups != null) {
            return groups;
        }
        return previewGroups(previewRecords(dataset, header));
    }

    private List<WarframeCommunityDatasetPreviewSnapshotRecord> previewRecords(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetPreviewSnapshot snapshot) {
        if (dataset != WarframeCommunityDataset.RELIC
                && aggregateRoot(dataset) == WarframeCommunityDataset.RELIC) {
            return snapshot.records().stream()
                    .filter(record -> dataset.code().equals(
                            StrUtil.blankToDefault(record.targetDataset(), dataset.code())))
                    .toList();
        }
        return snapshot.records();
    }

    private static int count(
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
            WarframeCommunityRecordChangeType changeType) {
        return (int) records.stream()
                .filter(record -> record.changeType() == changeType)
                .count();
    }

    private static List<WarframeCommunityDatasetPreviewGroup> previewGroups(
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records) {
        Map<String, Map<WarframeCommunityRecordChangeType, LinkedHashSet<String>>> grouped =
                new LinkedHashMap<>();
        for (WarframeCommunityDatasetPreviewSnapshotRecord record : records) {
            if (record.changeType() == null || !record.changeType().isOperation()
                    || StrUtil.isBlank(record.operationId())) {
                continue;
            }
            String sourceCode = StrUtil.blankToDefault(record.sourceCode(), "unknown");
            grouped.computeIfAbsent(sourceCode, ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(record.changeType(), ignored -> new LinkedHashSet<>())
                    .add(record.operationId());
        }
        List<WarframeCommunityRecordChangeType> changeOrder = List.of(
                WarframeCommunityRecordChangeType.ADDED,
                WarframeCommunityRecordChangeType.CHANGED,
                WarframeCommunityRecordChangeType.REMOVED);
        return grouped.entrySet().stream()
                .map(source -> new WarframeCommunityDatasetPreviewGroup(
                        source.getKey(),
                        changeOrder.stream()
                                .filter(source.getValue()::containsKey)
                                .map(changeType -> new WarframeCommunityDatasetPreviewChangeGroup(
                                        changeType,
                                        List.copyOf(source.getValue().get(changeType))))
                                .toList()))
                .toList();
    }

    private static Map<String, List<WarframeCommunityDatasetPreviewGroup>> previewGroupsByDataset(
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
            String defaultDataset) {
        Map<String, List<WarframeCommunityDatasetPreviewSnapshotRecord>> recordsByDataset =
                new LinkedHashMap<>();
        for (WarframeCommunityDatasetPreviewSnapshotRecord record : records) {
            String dataset = StrUtil.blankToDefault(record.targetDataset(), defaultDataset);
            recordsByDataset.computeIfAbsent(dataset, ignored -> new ArrayList<>()).add(record);
        }
        Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset =
                new LinkedHashMap<>();
        recordsByDataset.forEach((dataset, datasetRecords) ->
                groupsByDataset.put(dataset, previewGroups(datasetRecords)));
        if (recordsByDataset.isEmpty()) {
            groupsByDataset.put(defaultDataset, List.of());
        }
        return Map.copyOf(groupsByDataset);
    }

    private static Map<String, WarframeCommunityDatasetPreviewSnapshotRecord> previewOperations(
            WarframeCommunityDatasetPreviewSnapshot snapshot) {
        return snapshot.records().stream().collect(Collectors.toMap(
                WarframeCommunityDatasetPreviewSnapshotRecord::operationId,
                record -> record,
                (first, duplicate) -> {
                    throw invalidFile("预览包含重复操作 ID");
                },
                LinkedHashMap::new));
    }

    private static Set<String> selectedOperationIds(
            WarframeCommunityDatasetMergeRequest request, Set<String> operationIds) {
        Set<String> selected = request.selectedOperationIds().stream()
                .filter(StrUtil::isNotBlank).map(String::trim).collect(Collectors.toSet());
        BusinessAssert.isTrue(selected.size() == request.selectedOperationIds().size(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "操作 ID 不能为空或在去除空白后重复");
        BusinessAssert.isTrue(operationIds.containsAll(selected),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "提交包含不属于该预览的操作 ID");
        return selected;
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetPreview merge(
            String datasetCode, WarframeCommunityDatasetMergeRequest request) {
        BusinessAssert.isTrue(request != null && request.selectedOperationIds() != null,
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "社区数据合并请求不能为空");
        WarframeCommunityDataset requestedDataset = parseDataset(datasetCode);
        WarframeCommunityDataset dataset = aggregateRoot(requestedDataset);
        LockHandle lock = datasetLock.acquire(dataset.code());
        try {
            BusinessAssert.isTrue(!isPreviewConsumed(
                            request.previewId(), dataset.code()),
                    WarframeManagementErrorCode.COMMUNITY_PREVIEW_ALREADY_MERGED);
            WarframeCommunityDatasetPreviewSnapshot snapshot =
                    loadPreviewSnapshot(request.previewId());
            BusinessAssert.isTrue(dataset.code().equals(snapshot.dataset()),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                "社区数据预览与当前数据集不匹配");
            if (dataset == WarframeCommunityDataset.RELIC) {
                return mergeRelicSnapshot(request, snapshot);
            }
            if (dataset == WarframeCommunityDataset.ARBITRATION_SCHEDULE) {
                return mergeArbitrationSnapshot(request, snapshot);
            }
            Map<String, WarframeCommunityDatasetPreviewSnapshotRecord> operations =
                    previewOperations(snapshot);
            Set<String> selected = selectedOperationIds(request, operations.keySet());
            List<WarframeCommunityDatasetRecord> records = all(dataset);
            Map<String, WarframeCommunityDatasetRecord> existing = new LinkedHashMap<>();
            for (WarframeCommunityDatasetRecord record : records) {
                String normalizedKey = normalizeKey(identity(dataset, record));
                if (existing.putIfAbsent(normalizedKey, record) != null) {
                    throw invalidFile(dataset.fileName() + "：数据库存在重复来源身份 "
                            + record.getSourceCode() + "/" + record.getRecordKey());
                }
            }
            int updated = 0;
            int added = 0;
            for (WarframeCommunityDatasetPreviewSnapshotRecord snapshotRecord
                    : snapshot.records()) {
                if (!selected.contains(snapshotRecord.operationId())) {
                    continue;
                }
                BusinessAssert.isTrue(
                        snapshotRecord.recordSource() == WarframeCommunityRecordSource.COMMUNITY
                                && snapshotRecord.changeType() != null
                                && snapshotRecord.changeType().isOperation()
                                && snapshotRecord.upstreamPresent()
                                == (snapshotRecord.changeType() != WarframeCommunityRecordChangeType.REMOVED),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE, "预览操作类型不合法");
                WarframeCommunityDatasetRecord record = existing.get(
                        normalizeKey(snapshotRecord.identityKey()));
                // 同来源身份和预览基线必须同时匹配，不能把过期候选应用到另一条记录。
                BusinessAssert.isTrue(snapshotRecord.id() == null
                                ? record == null
                                : record != null
                                && Objects.equals(record.getId(), snapshotRecord.id())
                                && readJson(record.getCurrentJson()).equals(
                                        readJson(snapshotRecord.originalJson())),
                        WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY,
                        "记录已发生变化，请重新生成预览");
                if (record == null && snapshotRecord.upstreamPresent()) {
                    WarframeCommunityDatasetRecord created = newRecord(
                            dataset,
                            snapshotRecord.recordKey(),
                            readJson(snapshotRecord.upstreamJson()),
                            effectiveRecordSource(
                                    dataset,
                                    WarframeCommunityRecordSource.COMMUNITY),
                            snapshotRecord.sourceCode());
                    created.setId(Long.valueOf(snapshotRecord.operationId()));
                    entityManager.persist(created);
                    existing.put(normalizeKey(identity(dataset, created)), created);
                    records.add(created);
                    added++;
                    continue;
                }
                if (record == null) {
                    continue;
                }
                if (isSelfMaintained(record)) {
                    // 自维护记录不属于上游同步范围，不能被覆盖、停用或删除。
                    continue;
                }
                if (!snapshotRecord.upstreamPresent()) {
                    entityManager.remove(record);
                    existing.remove(normalizeKey(identity(dataset, record)));
                    updated++;
                    continue;
                }
                JsonNode current = readJson(snapshotRecord.currentJson());
                record.setCurrentJson(snapshotRecord.currentJson());
                projectionMapper.sync(record, current);
                record.setManualFieldsJson("{}");
                record.setManualModified(false);
                record.setUpstreamJson(snapshotRecord.upstreamJson());
                record.setUpstreamHash(hash(record.getUpstreamJson()));
                record.setSourceHash(record.getUpstreamHash());
                record.setSourceCode(snapshotRecord.sourceCode());
                record.setLastImportedAt(LocalDateTime.now());
                record.setEnabled(snapshotRecord.enabled());
                updated++;
            }
            WarframeCommunityMergeReceipt receipt = new WarframeCommunityMergeReceipt();
            receipt.setPreviewId(request.previewId().trim());
            receipt.setDataset(dataset.code());
            receipt.setCommittedAt(Instant.now());
            entityManager.persist(receipt);
            itemIndexInvalidator.invalidateAfterCommit();
            consumePreviewAfterCommit(request.previewId(), snapshot.dataset());
            records = all(dataset);
            return previewResponse(
                    snapshot.previewId(),
                    snapshot.createdAt(),
                    snapshot.expiresAt(),
                    dataset,
                    "merge",
                    records.size(),
                    added,
                    updated,
                    snapshot.conflictCount(),
                    snapshot.diffs(),
                    records.stream().map(record -> toItem(dataset, record)).toList(),
                    snapshot.sources());
        } finally {
            datasetLock.releaseAfterTransaction(lock);
        }
    }

    private WarframeCommunityDatasetPreview mergeArbitrationSnapshot(
            WarframeCommunityDatasetMergeRequest request,
            WarframeCommunityDatasetPreviewSnapshot snapshot) {
        validateArbitrationSnapshot(snapshot);
        Map<String, WarframeCommunityDatasetPreviewSnapshotRecord> operations =
                previewOperations(snapshot);
        Set<String> selected = selectedOperationIds(request, operations.keySet());
        List<ArbitrationScheduleEntry> applied = new ArrayList<>();
        List<Instant> deleted = new ArrayList<>();
        for (String operationId : selected) {
            WarframeCommunityDatasetPreviewSnapshotRecord record = operations.get(operationId);
            Instant activation = Instant.parse(record.recordKey());
            WarframeArbitrationSchedule current =
                    arbitrationScheduleRepository.findById(activation).orElse(null);
            String currentJson = current == null ? "{}" : writeJson(arbitrationJson(toEntry(current)));
            BusinessAssert.isTrue(readJson(currentJson).equals(readJson(record.originalJson()))
                            || (current == null && "{}".equals(record.originalJson())),
                    WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY,
                    "仲裁排期已变化，请重新生成预览");
            if (!record.upstreamPresent()) {
                if (current != null) {
                    deleted.add(activation);
                }
                continue;
            }
            try {
                JsonNode upstreamJson = readJson(record.upstreamJson());
                String id = arbitrationText(upstreamJson, "id");
                Instant upstreamActivation = Instant.parse(
                        arbitrationText(upstreamJson, "activation"));
                Instant expiry = Instant.parse(arbitrationText(upstreamJson, "expiry"));
                BusinessAssert.isTrue(activation.equals(upstreamActivation),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期上游 activation 与 recordKey 不一致");
                BusinessAssert.isTrue(Duration.between(upstreamActivation, expiry)
                                .equals(Duration.ofHours(1)),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期时间区间必须为一小时");
                JsonNode bounds = upstreamJson.get("bounds");
                BusinessAssert.isTrue(bounds != null && bounds.isObject(),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期 bounds 必须是 JSON 对象");
                applied.add(new ArbitrationScheduleEntry(
                        id,
                        upstreamActivation,
                        expiry,
                        arbitrationText(upstreamJson, "node"),
                        arbitrationText(upstreamJson, "missionType"),
                        arbitrationText(upstreamJson, "enemy"),
                        arbitrationText(upstreamJson, "planet"),
                        writeJson(bounds)));
            } catch (RuntimeException exception) {
                throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期预览记录无效", exception);
            }
        }
        arbitrationStore.merge(applied, deleted, snapshot.source(), Instant.now());
        WarframeCommunityMergeReceipt receipt = new WarframeCommunityMergeReceipt();
        receipt.setPreviewId(request.previewId().trim());
        receipt.setDataset(snapshot.dataset());
        receipt.setCommittedAt(Instant.now());
        entityManager.persist(receipt);
        consumePreviewAfterCommit(request.previewId(), snapshot.dataset());
        return previewResponse(
                snapshot.previewId(),
                snapshot.createdAt(),
                snapshot.expiresAt(),
                WarframeCommunityDataset.ARBITRATION_SCHEDULE,
                "merge",
                arbitrationScheduleRepository.findAllByOrderByActivationAsc().size(),
                applied.size(),
                applied.size(),
                0,
                snapshot.diffs(),
                arbitrationScheduleRepository.findAllByOrderByActivationAsc().stream()
                        .map(this::arbitrationItem)
                        .toList(),
                snapshot.sources());
    }

    private void validateArbitrationSnapshot(
            WarframeCommunityDatasetPreviewSnapshot snapshot) {
        BusinessAssert.isTrue(WarframeCommunityDataset.ARBITRATION_SCHEDULE.code()
                        .equals(snapshot.dataset()),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "仲裁排期预览数据集不正确");
        String expectedSource = WarframeCommunitySource.BROWSE_WF_ARBITRATION.code();
        BusinessAssert.isTrue(expectedSource.equals(snapshot.source()),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "仲裁排期预览来源不正确");
        BusinessAssert.isTrue(snapshot.sourceCodes() != null
                        && snapshot.sourceCodes().contains(expectedSource),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "仲裁排期预览缺少来源范围");
        for (WarframeCommunityDatasetPreviewSnapshotRecord record : snapshot.records()) {
            BusinessAssert.isTrue(
                    WarframeCommunityDataset.ARBITRATION_SCHEDULE.code()
                            .equals(record.targetDataset())
                            && record.recordSource() == WarframeCommunityRecordSource.COMMUNITY
                            && expectedSource.equals(record.sourceCode())
                            && StrUtil.isNotBlank(record.operationId())
                            && StrUtil.isNotBlank(record.recordKey())
                            && record.recordKey().equals(record.identityKey())
                            && record.changeType() != null
                            && record.changeType().isOperation()
                            && record.upstreamPresent()
                            == (record.changeType() != WarframeCommunityRecordChangeType.REMOVED),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "仲裁排期预览记录身份或变更类型不合法");
            Instant activation;
            try {
                activation = Instant.parse(record.recordKey());
            } catch (RuntimeException exception) {
                throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期预览 recordKey 不是合法时间戳", exception);
            }
            validateJsonObject(record.currentJson(), "仲裁排期当前 JSON");
            validateJsonObject(record.originalJson(), "仲裁排期原始 JSON");
            if (!record.upstreamPresent()) {
                BusinessAssert.isTrue("{}".equals(readJson(record.upstreamJson()).toString()),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "删除型仲裁排期预览的 upstreamJson 必须为空对象");
                continue;
            }
            JsonNode upstream = validateJsonObject(record.upstreamJson(), "仲裁排期上游 JSON");
            try {
                BusinessAssert.isTrue(activation.equals(Instant.parse(
                                arbitrationText(upstream, "activation"))),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期上游 activation 与 recordKey 不一致");
                Instant expiry = Instant.parse(arbitrationText(upstream, "expiry"));
                BusinessAssert.isTrue(Duration.between(activation, expiry)
                                .equals(Duration.ofHours(1)),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期时间区间必须为一小时");
            } catch (BusinessException exception) {
                throw exception;
            } catch (RuntimeException exception) {
                throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "仲裁排期预览时间字段不合法", exception);
            }
            BusinessAssert.isTrue(upstream.get("bounds") != null
                            && upstream.get("bounds").isObject(),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "仲裁排期 bounds 必须是 JSON 对象");
            arbitrationText(upstream, "id");
            arbitrationText(upstream, "node");
            arbitrationText(upstream, "missionType");
            arbitrationText(upstream, "enemy");
            arbitrationText(upstream, "planet");
        }
    }

    private JsonNode validateJsonObject(String json, String description) {
        JsonNode value = readJson(json);
        BusinessAssert.isTrue(value.isObject(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                description + "必须是 JSON 对象");
        return value;
    }

    private static String arbitrationText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        BusinessAssert.isTrue(value != null && !value.isNull() && StrUtil.isNotBlank(value.asText()),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "仲裁排期预览缺少字段：" + field);
        return value.asText();
    }

    private WarframeCommunityDatasetRecord arbitrationPreviewRecord(
            WarframeArbitrationSchedule record) {
        WarframeCommunityDatasetRecord value = new WarframeCommunityDatasetRecord() {
        };
        value.setRecordKey(record.getActivation().toString());
        value.setSourceCode("browse.wf:arbys.txt");
        value.setRecordSource(WarframeCommunityRecordSource.COMMUNITY);
        value.setEnabled(true);
        value.setLastImportedAt(record.getFetchedAt() == null
                ? null : LocalDateTime.ofInstant(record.getFetchedAt(),
                        ZoneOffset.UTC));
        return value;
    }

    private WarframeCommunityDatasetPreview mergeRelicSnapshot(
            WarframeCommunityDatasetMergeRequest request,
            WarframeCommunityDatasetPreviewSnapshot snapshot) {
        Map<String, WarframeCommunityDatasetPreviewSnapshotRecord> operations =
                previewOperations(snapshot);
        Set<String> selected = selectedOperationIds(request, operations.keySet());

        Map<WarframeCommunityDataset, Map<String, WarframeCommunityDatasetRecord>>
                existingByDataset = new LinkedHashMap<>();
        for (WarframeCommunityDataset target : relicDatasets()) {
            Map<String, WarframeCommunityDatasetRecord> existing = new LinkedHashMap<>();
            for (WarframeCommunityDatasetRecord record : all(target)) {
                String identity = normalizeKey(identity(target, record));
                if (existing.putIfAbsent(identity, record) != null) {
                    throw invalidFile(target.fileName() + "：数据库存在重复来源身份 "
                            + record.getSourceCode() + "/" + record.getRecordKey());
                }
            }
            existingByDataset.put(target, existing);
        }

        int updated = 0;
        int added = 0;
        for (WarframeCommunityDatasetPreviewSnapshotRecord snapshotRecord
                : snapshot.records()) {
            if (!selected.contains(snapshotRecord.operationId())) {
                continue;
            }
            BusinessAssert.isTrue(
                    snapshotRecord.recordSource() == WarframeCommunityRecordSource.COMMUNITY
                            && snapshotRecord.changeType() != null
                            && snapshotRecord.changeType().isOperation()
                            && snapshotRecord.upstreamPresent()
                            == (snapshotRecord.changeType()
                            != WarframeCommunityRecordChangeType.REMOVED),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE, "预览操作类型不合法");
            WarframeCommunityDataset target = parseDataset(
                    StrUtil.blankToDefault(snapshotRecord.targetDataset(),
                            WarframeCommunityDataset.RELIC.code()));
            BusinessAssert.isTrue(relicDatasets().contains(target),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "遗物预览包含未知目标数据集");
            Map<String, WarframeCommunityDatasetRecord> existing =
                    existingByDataset.get(target);
            WarframeCommunityDatasetRecord record = existing.get(
                    normalizeKey(snapshotRecord.identityKey()));
            // 预览基线同时校验主键和 JSON，避免把过期操作应用到另一条记录。
            BusinessAssert.isTrue(snapshotRecord.id() == null
                            ? record == null
                            : record != null
                            && Objects.equals(record.getId(), snapshotRecord.id())
                            && readJson(record.getCurrentJson()).equals(
                                    readJson(snapshotRecord.originalJson())),
                    WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY,
                    "记录已发生变化，请重新生成预览");
            if (record == null && snapshotRecord.upstreamPresent()) {
                WarframeCommunityDatasetRecord created = newRecord(
                        target,
                        snapshotRecord.recordKey(),
                        readJson(snapshotRecord.upstreamJson()),
                        WarframeCommunityRecordSource.COMMUNITY,
                        snapshotRecord.sourceCode());
                created.setId(Long.valueOf(snapshotRecord.operationId()));
                entityManager.persist(created);
                existing.put(normalizeKey(identity(target, created)), created);
                added++;
                continue;
            }
            if (record == null) {
                continue;
            }
            if (isSelfMaintained(record)) {
                // 自维护记录不属于上游同步范围，不能被覆盖或删除。
                continue;
            }
            if (!snapshotRecord.upstreamPresent()) {
                entityManager.remove(record);
                existing.remove(normalizeKey(identity(target, record)));
                updated++;
                continue;
            }
            JsonNode current = readJson(snapshotRecord.currentJson());
            record.setCurrentJson(snapshotRecord.currentJson());
            projectionMapper.sync(record, current);
            record.setManualFieldsJson("{}");
            record.setManualModified(false);
            record.setUpstreamJson(snapshotRecord.upstreamJson());
            record.setUpstreamHash(hash(record.getUpstreamJson()));
            record.setSourceHash(record.getUpstreamHash());
            record.setSourceCode(snapshotRecord.sourceCode());
            record.setLastImportedAt(LocalDateTime.now());
            record.setEnabled(snapshotRecord.enabled());
            updated++;
        }

        WarframeCommunityMergeReceipt receipt = new WarframeCommunityMergeReceipt();
        receipt.setPreviewId(request.previewId().trim());
        receipt.setDataset(WarframeCommunityDataset.RELIC.code());
        receipt.setCommittedAt(Instant.now());
        entityManager.persist(receipt);
        itemIndexInvalidator.invalidateAfterCommit();
        consumePreviewAfterCommit(request.previewId(), snapshot.dataset());

        List<WarframeCommunityDatasetItem> records = relicDatasets().stream()
                .flatMap(target -> all(target).stream()
                        .map(record -> toItem(target, record)))
                .toList();
        return previewResponse(
                snapshot.previewId(),
                snapshot.createdAt(),
                snapshot.expiresAt(),
                WarframeCommunityDataset.RELIC,
                "merge",
                records.size(),
                added,
                updated,
                snapshot.conflictCount(),
                snapshot.diffs(),
                records, snapshot.sources());
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetItem updateManual(
            String datasetCode, long id, String currentJson) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        assertManualManagementAllowed(dataset);
        LockHandle lock = datasetLock.acquire(aggregateRoot(dataset).code());
        try {
            WarframeCommunityDatasetRecord record = entityManager.find(dataset.entityType(), id);
            if (record == null) {
                throw new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND);
            }
            BusinessAssert.isTrue(isSelfMaintained(record),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "外部来源记录不允许人工修改，请新增自维护记录进行补充");
            ObjectNode after = readObject(currentJson);
            validateDatasetJson(dataset, after);
            if (dataset == WarframeCommunityDataset.TRANSLATION
                    || dataset == WarframeCommunityDataset.REWARD_POOL) {
                BusinessAssert.isTrue(record.getRecordKey().equals(recordKey(dataset, after))
                                && Objects.equals(locale(record), locale(after)),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "完整业务键和语言不可修改，请新增记录");
            }
            String json = writeJson(after);
            record.setCurrentJson(json);
            projectionMapper.sync(record, after);
            record.setManualFieldsJson("{}");
            record.setManualModified(false);
            record.setSourceHash(hash(json));
            entityManager.merge(record);
            itemIndexInvalidator.invalidateAfterCommit();
            return toItem(dataset, record);
        } finally {
            datasetLock.releaseAfterTransaction(lock);
        }
    }

    @Override
    @Transactional
    public void deleteManual(String datasetCode, long id) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        assertManualManagementAllowed(dataset);
        LockHandle lock = datasetLock.acquire(aggregateRoot(dataset).code());
        try {
            WarframeCommunityDatasetRecord record = entityManager.find(dataset.entityType(), id);
            if (record == null) {
                throw new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND);
            }
            if (isReferencedByPendingPreview(dataset, record)) {
                throw new BusinessException(
                        WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY,
                        "记录正在待确认预览中，暂不能人工删除");
            }
            BusinessAssert.isTrue(isSelfMaintained(record),
                    WarframeManagementErrorCode.COMMUNITY_DATASET_READ_ONLY,
                    "外部来源记录不允许人工删除，请通过刷新预览采纳上游删除");
            entityManager.remove(record);
            itemIndexInvalidator.invalidateAfterCommit();
        } finally {
            datasetLock.releaseAfterTransaction(lock);
        }
    }

    private static List<WarframeCommunityDataset> relicDatasets() {
        return List.of(
                WarframeCommunityDataset.RELIC,
                WarframeCommunityDataset.RELIC_REWARD,
                WarframeCommunityDataset.RELIC_DROP);
    }

    private static void assertManualManagementAllowed(WarframeCommunityDataset dataset) {
        BusinessAssert.isTrue(dataset.metadata().manageable() && !dataset.metadata().readOnly(),
                WarframeManagementErrorCode.COMMUNITY_DATASET_READ_ONLY,
                dataset.label() + "仅支持社区来源刷新和预览采纳，不支持人工新增、上传、修改或删除");
    }

    private static WarframeCommunityDataset aggregateRoot(WarframeCommunityDataset dataset) {
        return relicDatasets().contains(dataset)
                ? WarframeCommunityDataset.RELIC : dataset;
    }

    private WarframeCommunityDatasetPreview reconcile(
            WarframeCommunityDataset dataset,
            String source,
            List<JsonNode> incoming) {
        String sourceCode = previewSourceCode(dataset, source);
        List<SourceRecord> records = incoming.stream()
                .map(record -> new SourceRecord(sourceCode, record))
                .toList();
        return reconcileSourceRecords(dataset, source, records, Set.of(sourceCode));
    }

    private WarframeCommunityDatasetPreview reconcileSourceRecords(
            WarframeCommunityDataset dataset,
            String source,
            List<SourceRecord> incoming,
            Set<String> refreshedSources) {
        return reconcileSourceRecords(dataset, source, incoming, refreshedSources, List.of());
    }

    private WarframeCommunityDatasetPreview reconcileSourceRecords(
            WarframeCommunityDataset dataset,
            String source,
            List<SourceRecord> incoming,
            Set<String> refreshedSources,
            List<WarframeCommunitySourceSummary> sources) {
        DatasetReconciliation result = reconcileDataset(dataset, incoming, refreshedSources);
        return savePreview(dataset, source, result.recordCount(), result.addedCount(),
                result.changedCount(), 0, List.of(), result.operations(), refreshedSources, sources);
    }

    private List<JsonNode> validateSourceRecords(
            WarframeCommunityDataset dataset, String sourceCode, List<JsonNode> records) {
        Set<String> identities = new HashSet<>();
        for (JsonNode record : records) {
            if (!identities.add(incomingIdentity(dataset, sourceCode, record))) {
                throw invalidFile(dataset.fileName() + "：上游存在重复记录键 "
                        + recordKey(dataset, record));
            }
        }
        return records;
    }

    private DatasetReconciliation reconcileDataset(
            WarframeCommunityDataset dataset,
            List<SourceRecord> incoming,
            Set<String> refreshedSources) {
        if (refreshedSources.isEmpty()) {
            return new DatasetReconciliation(0, 0, 0, List.of());
        }
        Map<String, WarframeCommunityDatasetRecord> existing = new LinkedHashMap<>();
        for (WarframeCommunityDatasetRecord record : all(dataset)) {
            String normalizedKey = normalizeKey(identity(dataset, record));
            if (existing.putIfAbsent(normalizedKey, record) != null) {
                throw invalidFile(dataset.fileName() + "：数据库存在重复来源身份 "
                        + record.getSourceCode() + "/" + record.getRecordKey());
            }
        }
        // 同源同语言重复身份属于上游错误，不能静默选择其中一条。
        Map<String, JsonNode> uniqueIncoming = new LinkedHashMap<>();
        for (SourceRecord incomingRecord : incoming) {
            JsonNode upstream = incomingRecord.value();
            String key = recordKey(dataset, upstream);
            String normalizedKey = incomingIdentity(dataset, incomingRecord.sourceCode(), upstream);
            if (uniqueIncoming.putIfAbsent(normalizedKey, upstream) != null) {
                throw invalidFile(dataset.fileName() + "：上游存在重复记录键 " + key);
            }
        }
        Map<String, String> incomingSources = incoming.stream().collect(Collectors.toMap(
                item -> incomingIdentity(dataset, item.sourceCode(), item.value()),
                SourceRecord::sourceCode,
                (first, ignored) -> first,
                LinkedHashMap::new));
        List<WarframeCommunityDatasetItem> previewRecords = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int added = 0;
        int changed = 0;
        for (Map.Entry<String, JsonNode> entry : uniqueIncoming.entrySet()) {
            JsonNode upstream = entry.getValue();
            String sourceCode = incomingSources.get(entry.getKey());
            String key = recordKey(dataset, upstream);
            String normalizedKey = incomingIdentity(dataset, sourceCode, upstream);
            seen.add(normalizedKey);
            WarframeCommunityDatasetRecord current = existing.get(normalizedKey);
            if (current == null) {
                WarframeCommunityDatasetRecord created = newRecord(
                        dataset, key, upstream,
                        effectiveRecordSource(dataset, WarframeCommunityRecordSource.COMMUNITY),
                        sourceCode);
                added++;
                previewRecords.add(toItem(dataset, created, true, true,
                        WarframeCommunityRecordChangeType.ADDED));
                continue;
            }
            if (isSelfMaintained(current)) {
                // 自维护记录不属于上游同步范围，也不进入本次预览。
                continue;
            }
            String upstreamJson = writeJson(upstream);
            if (upstream.equals(readJson(current.getCurrentJson())) && current.isEnabled()) {
                continue;
            }
            changed++;
            WarframeCommunityDatasetRecord previewRecord = copyRecord(dataset, current);
            previewRecord.setOriginalJson(current.getCurrentJson());
            previewRecord.setCurrentJson(upstreamJson);
            projectionMapper.sync(previewRecord, upstream);
            previewRecord.setManualModified(false);
            previewRecord.setManualFieldsJson("{}");
            previewRecord.setUpstreamJson(upstreamJson);
            previewRecord.setUpstreamHash(hash(upstreamJson));
            previewRecord.setSourceHash(previewRecord.getUpstreamHash());
            previewRecord.setSourceCode(sourceCode);
            previewRecord.setLastImportedAt(LocalDateTime.now());
            previewRecord.setEnabled(true);
            previewRecords.add(toItem(dataset, previewRecord, true, true,
                    WarframeCommunityRecordChangeType.CHANGED));
        }
        for (WarframeCommunityDatasetRecord record : existing.values()) {
            if (!refreshedSources.contains(recordSourceCode(dataset, record))
                    || seen.contains(normalizeKey(identity(dataset, record)))
                    || isSelfMaintained(record)) {
                continue;
            }
            WarframeCommunityDatasetRecord previewRecord = copyRecord(dataset, record);
            previewRecord.setOriginalJson(record.getCurrentJson());
            previewRecords.add(toItem(dataset, previewRecord, false, false,
                    WarframeCommunityRecordChangeType.REMOVED));
        }
        return new DatasetReconciliation(uniqueIncoming.size(), added, changed, previewRecords);
    }

    private record DatasetReconciliation(
            int recordCount, int addedCount, int changedCount,
            List<WarframeCommunityDatasetItem> operations) {
    }

    private WarframeCommunityDatasetPreview reconcileRelicSourceRecords(
            String source,
            Map<WarframeCommunityDataset, List<SourceRecord>> incomingByDataset,
            Map<WarframeCommunityDataset, Set<String>> refreshedSources) {
        return reconcileRelicSourceRecords(source, incomingByDataset, refreshedSources, List.of());
    }

    private WarframeCommunityDatasetPreview reconcileRelicSourceRecords(
            String source,
            Map<WarframeCommunityDataset, List<SourceRecord>> incomingByDataset,
            Map<WarframeCommunityDataset, Set<String>> refreshedSources,
            List<WarframeCommunitySourceSummary> sourceSummaries) {
        List<WarframeCommunityDatasetItem> previewRecords = new ArrayList<>();
        int added = 0;
        int changed = 0;
        int recordCount = 0;
        for (WarframeCommunityDataset target : relicDatasets()) {
            DatasetReconciliation result = reconcileDataset(target,
                    incomingByDataset.getOrDefault(target, List.of()),
                    refreshedSources.getOrDefault(target, Set.of()));
            recordCount += result.recordCount();
            added += result.addedCount();
            changed += result.changedCount();
            previewRecords.addAll(result.operations());
        }
        return savePreview(WarframeCommunityDataset.RELIC, source, recordCount, added, changed,
                0, List.of(), previewRecords,
                refreshedSources.values().stream().flatMap(Set::stream)
                        .collect(Collectors.toCollection(LinkedHashSet::new)), sourceSummaries);
    }

    private WarframeCommunityDatasetRecord newRecord(
            WarframeCommunityDataset dataset,
            String key,
            JsonNode upstream,
            WarframeCommunityRecordSource recordSource) {
        return newRecord(dataset, key, upstream, recordSource, null);
    }

    private WarframeCommunityDatasetRecord newRecord(
            WarframeCommunityDataset dataset,
            String key,
            JsonNode upstream,
            WarframeCommunityRecordSource recordSource,
            String sourceCode) {
        if (dataset == WarframeCommunityDataset.ARBITRATION_SCHEDULE) {
            throw new IllegalStateException("仲裁排期必须使用专用排期表合并");
        }
        recordSource = effectiveRecordSource(dataset, recordSource);
        if (dataset == WarframeCommunityDataset.MARKET_COMPONENT) {
            ObjectNode normalized = readObject(writeJson(upstream));
            validateDatasetJson(dataset, normalized);
            upstream = normalized;
            if (StrUtil.isBlank(key)) {
                key = recordKey(dataset, normalized);
            }
        }
        WarframeCommunityDatasetRecord record;
        try {
            record = dataset.entityType().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("无法创建社区数据集记录：" + dataset.code(), exception);
        }
        String json = writeJson(upstream);
        String upstreamJson = recordSource == WarframeCommunityRecordSource.COMMUNITY
                ? json : "{}";
        String resolvedSourceCode = StrUtil.isNotBlank(sourceCode)
                ? sourceCode : sourceCode(dataset, recordSource);
        record.setRecordKey(key);
        record.setRecordSource(recordSource);
        record.setSourceCode(resolvedSourceCode);
        record.setLastImportedAt(recordSource == WarframeCommunityRecordSource.COMMUNITY
                ? LocalDateTime.now() : null);
        record.setCurrentJson(json);
        projectionMapper.sync(record, upstream);
        record.setUpstreamJson(upstreamJson);
        record.setOriginalJson(json);
        record.setManualFieldsJson("{}");
        record.setUpstreamHash(hash(upstreamJson));
        record.setSourceHash(hash(json));
        record.setManualModified(false);
        record.setEnabled(true);
        return record;
    }

    /** 创建只存在于 Redis 的临时预览，不修改数据库中的托管实体。 */
    private WarframeCommunityDatasetPreview savePreview(
            WarframeCommunityDataset dataset,
            String source,
            int recordCount,
            int addedCount,
            int changedCount,
            int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetItem> records,
            Set<String> sourceCodes) {
        return savePreview(dataset, source, recordCount, addedCount, changedCount, conflictCount,
                diffs, records, sourceCodes, List.of());
    }

    private WarframeCommunityDatasetPreview savePreview(
            WarframeCommunityDataset dataset,
            String source,
            int recordCount,
            int addedCount,
            int changedCount,
            int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetItem> records,
            Set<String> sourceCodes,
            List<WarframeCommunitySourceSummary> sourceSummaries) {
        List<WarframeCommunityDatasetItem> operations = records.stream()
                .filter(item -> item.changeType() != null && item.changeType().isOperation())
                .toList();
        List<WarframeCommunitySourceSummary> summaries = withSourceOperations(
                sourceSummaries, operations.stream()
                        .map(WarframeCommunityDatasetItem::sourceCode).toList());
        String previewId = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(apiProperties.getPreviewRetention());
        List<WarframeCommunityDatasetPreviewSnapshotRecord> snapshotRecords =
                operations.stream().map(this::snapshotRecord).toList();
        Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset =
                previewGroupsByDataset(snapshotRecords, dataset.code());
        WarframeCommunityDatasetPreviewSnapshot snapshot =
                new WarframeCommunityDatasetPreviewSnapshot(
                        previewId, dataset.code(), source, createdAt, expiresAt,
                        recordCount, addedCount, changedCount, conflictCount, diffs,
                        snapshotRecords, WarframeCommunityPreviewStatus.PENDING,
                        Set.copyOf(sourceCodes), 0, 0, groupsByDataset, List.of(), summaries);
        previewStore.save(snapshot);
        return previewResponse(previewId, createdAt, expiresAt, dataset, source,
                recordCount, addedCount, changedCount, conflictCount, diffs, operations, summaries);
    }

    private WarframeCommunityDatasetPreview previewResponse(
            String previewId,
            Instant createdAt,
            Instant expiresAt,
            WarframeCommunityDataset dataset,
            String source,
            int recordCount,
            int addedCount,
            int changedCount,
            int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetItem> records,
            List<WarframeCommunitySourceSummary> sources) {
        return new WarframeCommunityDatasetPreview(
                previewId, createdAt, expiresAt, dataset.code(), source, recordCount,
                addedCount, changedCount, conflictCount, diffs, records, sources);
    }

    private WarframeCommunityDatasetPreviewSnapshotRecord snapshotRecord(
            WarframeCommunityDatasetItem item) {
        return new WarframeCommunityDatasetPreviewSnapshotRecord(
                item.id(), item.operationId(), item.dataset(), item.recordKey(),
                item.identityKey(), item.locale(),
                item.upstreamJson(), item.upstreamPresent(),
                item.currentJson(), item.originalJson(), item.manualFieldsJson(),
                item.manualModified(), item.enabled(), item.recordSource(),
                item.sourceCode(), item.lastImportedAt(),
                item.changeType(),
                item.createdAt(), item.updatedAt());
    }

    private WarframeCommunityDatasetItem snapshotItem(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetPreviewSnapshotRecord snapshotRecord) {
        WarframeCommunityDataset targetDataset = parseDataset(
                StrUtil.blankToDefault(snapshotRecord.targetDataset(), dataset.code()));
        if (targetDataset == WarframeCommunityDataset.ARBITRATION_SCHEDULE) {
            return arbitrationSnapshotItem(snapshotRecord);
        }
        WarframeCommunityDatasetRecord record = newRecord(
                targetDataset, snapshotRecord.recordKey(), readJson(snapshotRecord.currentJson()),
                snapshotRecord.recordSource(), snapshotRecord.sourceCode());
        record.setId(snapshotRecord.id());
        record.setCurrentJson(snapshotRecord.currentJson());
        record.setOriginalJson(snapshotRecord.originalJson());
        record.setUpstreamJson(snapshotRecord.upstreamJson());
        record.setManualFieldsJson(snapshotRecord.manualFieldsJson());
        record.setManualModified(snapshotRecord.manualModified());
        record.setEnabled(snapshotRecord.enabled());
        record.setSourceCode(snapshotRecord.sourceCode());
        record.setLastImportedAt(snapshotRecord.lastImportedAt());
        record.setCreatedAt(snapshotRecord.createdAt());
        record.setUpdatedAt(snapshotRecord.updatedAt());
        projectionMapper.sync(record, readJson(snapshotRecord.currentJson()));
        return toItem(targetDataset, record, snapshotRecord.upstreamPresent(), snapshotRecord.enabled(),
                snapshotRecord.operationId(), snapshotRecord.changeType());
    }

    private WarframeCommunityDatasetItem arbitrationSnapshotItem(
            WarframeCommunityDatasetPreviewSnapshotRecord snapshotRecord) {
        JsonNode current = readJson(snapshotRecord.currentJson());
        Map<String, Object> values = new LinkedHashMap<>();
        current.fields().forEachRemaining(entry -> values.put(entry.getKey(),
                objectMapper.convertValue(entry.getValue(), Object.class)));
        return new WarframeCommunityDatasetItem(
                snapshotRecord.id(),
                snapshotRecord.operationId(),
                WarframeCommunityDataset.ARBITRATION_SCHEDULE.code(),
                snapshotRecord.recordKey(),
                snapshotRecord.identityKey(),
                snapshotRecord.locale(),
                snapshotRecord.recordSource(),
                snapshotRecord.sourceCode(),
                snapshotRecord.lastImportedAt(),
                new WarframeCommunityGenericDetails(values),
                snapshotRecord.upstreamJson(),
                snapshotRecord.currentJson(),
                snapshotRecord.originalJson(),
                snapshotRecord.manualFieldsJson(),
                snapshotRecord.manualModified(),
                snapshotRecord.upstreamPresent(),
                snapshotRecord.enabled(),
                snapshotRecord.changeType(),
                snapshotRecord.createdAt(),
                snapshotRecord.updatedAt());
    }

    private WarframeCommunityDatasetRecord copyRecord(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord source) {
        WarframeCommunityDatasetRecord copy = newRecord(
                dataset, source.getRecordKey(), readJson(source.getCurrentJson()),
                source.getRecordSource(), source.getSourceCode());
        copy.setId(source.getId());
        copy.setCurrentJson(source.getCurrentJson());
        copy.setUpstreamJson(source.getUpstreamJson());
        copy.setOriginalJson(source.getOriginalJson());
        copy.setManualFieldsJson(source.getManualFieldsJson());
        copy.setUpstreamHash(source.getUpstreamHash());
        copy.setSourceHash(source.getSourceHash());
        copy.setSourceCode(source.getSourceCode());
        copy.setLastImportedAt(source.getLastImportedAt());
        copy.setManualModified(source.isManualModified());
        copy.setEnabled(source.isEnabled());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

    private WarframeCommunityDatasetPreviewSnapshot loadPreviewSnapshot(String previewId) {
        return previewStore.assemble(previewStore.header(previewId));
    }

    private void consumePreviewAfterCommit(String previewId, String dataset) {
        Runnable consume = () -> previewStore.consume(previewId, dataset);
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

    private boolean isPreviewConsumed(String previewId, String dataset) {
        BusinessAssert.isTrue(StrUtil.isNotBlank(previewId),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        if (entityManager.find(WarframeCommunityMergeReceipt.class, previewId.trim()) != null) {
            previewStore.evict(previewId.trim());
            return true;
        }
        boolean consumed = previewStore.isConsumed(previewId.trim());
        if (consumed) {
            previewStore.evict(previewId.trim());
        }
        return consumed;
    }

    private WarframeCommunityDatasetPreview pendingPreview(WarframeCommunityDataset dataset) {
        WarframeCommunityDataset lockDataset = aggregateRoot(dataset);
        String previewId = previewStore.pendingPreviewId(lockDataset.code());
        if (StrUtil.isBlank(previewId)) {
            return null;
        }
        if (isPreviewConsumed(previewId, lockDataset.code())) {
            previewStore.clearPending(lockDataset.code());
            return null;
        }
        try {
            return preview(dataset.code(), previewId);
        } catch (BusinessException exception) {
            if (exception.getCode()
                    != WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND.getCode()) {
                throw exception;
            }
            previewStore.clearPending(lockDataset.code());
            return null;
        }
    }

    private boolean isReferencedByPendingPreview(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record) {
        WarframeCommunityDatasetPreview pending = pendingPreview(dataset);
        if (pending == null) {
            return false;
        }
        WarframeCommunityDatasetPreviewSnapshot snapshot = loadPreviewSnapshot(pending.previewId());
        // 旧版快照缺少来源范围时保守保护整个数据集，包含未产生差异的记录。
        return snapshot.sourceCodes() == null || snapshot.sourceCodes().isEmpty()
                || snapshot.sourceCodes().contains(recordSourceCode(dataset, record));
    }

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset, WarframeCommunityDatasetRecord record) {
        return toItem(dataset, record, true, record.isEnabled());
    }

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record,
            boolean upstreamPresent,
            boolean enabled) {
        return toItem(dataset, record, upstreamPresent, enabled, null);
    }

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record,
            boolean upstreamPresent,
            boolean enabled,
            WarframeCommunityRecordChangeType changeType) {
        return toItem(dataset, record, upstreamPresent, enabled,
                operationId(record, changeType), changeType);
    }

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record,
            boolean upstreamPresent,
            boolean enabled,
            String operationId,
            WarframeCommunityRecordChangeType changeType) {
        String upstreamJson = upstreamPresent ? record.getUpstreamJson() : "{}";
        String locale = locale(record);
        String identityKey = identity(dataset, record);
        return new WarframeCommunityDatasetItem(
                record.getId(), operationId, dataset.code(), record.getRecordKey(), identityKey, locale,
                record.getRecordSource(), record.getSourceCode(), record.getLastImportedAt(),
                projectionMapper.details(record),
                record.getCurrentJson(), upstreamJson, record.getOriginalJson(),
                record.getManualFieldsJson(), record.isManualModified(), enabled,
                upstreamPresent,
                changeType,
                record.getCreatedAt(), record.getUpdatedAt());
    }

    private String operationId(
            WarframeCommunityDatasetRecord record,
            WarframeCommunityRecordChangeType changeType) {
        if (changeType == null || !changeType.isOperation()) {
            return null;
        }
        if (record.getId() != null) {
            return Long.toString(record.getId());
        }
        return Long.toString(nextRecordId());
    }

    private long nextRecordId() {
        if (ids == null) {
            throw new IllegalStateException("社区记录写入需要 RDT 雪花 ID 服务");
        }
        return ids.next(PREVIEW_OPERATION_ID_SEQUENCE);
    }

    private boolean matches(WarframeCommunityDatasetRecord record, String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return true;
        }
        String value = keyword.trim().toLowerCase(Locale.ROOT);
        return record.getRecordKey().toLowerCase(Locale.ROOT).contains(value)
                || record.getCurrentJson().toLowerCase(Locale.ROOT).contains(value);
    }

    private static boolean isSelfMaintained(WarframeCommunityDatasetRecord record) {
        return record.getRecordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED;
    }

    private static String normalizeKey(String value) {
        // 记录键是上游协议标识，不能因为大小写变化把两条记录合并。
        return StrUtil.blankToDefault(value, "").trim();
    }

    private String identity(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record) {
        String recordKey = record.getRecordKey();
        if (record instanceof WarframeWorldStateSortie sortie
                && sortie.getRecordType() != null) {
            recordKey += "\u0000" + sortie.getRecordType().name();
        }
        return WarframeDatasetRecordIdentity.of(
                recordSourceCode(dataset, record), dataset.code(),
                recordKey, locale(record));
    }

    private String incomingIdentity(
            WarframeCommunityDataset dataset, String sourceCode, JsonNode record) {
        String key = recordKey(dataset, record);
        if (dataset == WarframeCommunityDataset.WORLDSTATE_SORTIE) {
            key += "\u0000" + record.path("recordType").asText();
        }
        return normalizeKey(WarframeDatasetRecordIdentity.of(
                sourceCode, dataset.code(), key, locale(record)));
    }

    private String recordSourceCode(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record) {
        if (isSelfMaintained(record)) {
            return "nnmbot:" + dataset.code();
        }
        return StrUtil.isNotBlank(record.getSourceCode())
                ? record.getSourceCode() : sourceCode(
                        dataset, WarframeCommunityRecordSource.COMMUNITY);
    }

    private String previewSourceCode(WarframeCommunityDataset dataset, String source) {
        // 上传只是刷新方式，不是新的上游来源；否则一次手工上传会把同数据集
        // 的正式来源全部判定为删除，并且无法复用已有记录的来源身份。
        String code = source.split("\\|", 2)[0];
        return WarframeCommunitySource.parse(dataset.code(), code).code();
    }

    private static String locale(JsonNode node) {
        if (node == null || !node.isObject()) {
            return null;
        }
        String locale = text(node, "locale");
        return StrUtil.isBlank(locale) ? null : locale;
    }

    private static String locale(WarframeCommunityDatasetRecord record) {
        if (record instanceof WarframeCommunityTranslationDataset value) {
            return value.getLocale();
        }
        if (record instanceof WarframeWorldStateRecord value) {
            return value.getLocale();
        }
        if (record instanceof WarframeWorldStateLanguage value) {
            return value.getLocale();
        }
        if (record instanceof WarframeCommunityWorldStateRule value) {
            return value.getLocale();
        }
        return null;
    }

    private List<WarframeCommunityDatasetRecord> all(WarframeCommunityDataset dataset) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        List<WarframeCommunityDatasetRecord> records = (List) entityManager.createQuery(
                "select distinct e from " + dataset.entityType().getSimpleName() + " e"
                        + (dataset == WarframeCommunityDataset.BOUNTY_DEFINITION
                                ? " left join fetch e.stages" : "")).getResultList();
        return new ArrayList<>(records);
    }

    List<JsonNode> parseRecords(
            WarframeCommunityDataset dataset, String json) {
        return parseRecords(dataset, json, null);
    }

    List<JsonNode> parseRecords(
            WarframeCommunityDataset dataset, String json, String defaultWorld) {
        return parseRecords(dataset, json, defaultWorld, null);
    }

    List<JsonNode> parseRecords(
            WarframeCommunityDataset dataset,
            String json,
            String defaultWorld,
            String sourceCode) {
        if (dataset == WarframeCommunityDataset.ARBITRATION_ESSENCE) {
            try {
                return ArbitrationEssenceParser.parse(json);
            } catch (IllegalArgumentException exception) {
                throw invalidFile(exception.getMessage());
            }
        }
        JsonNode root = readJson(json);
        BusinessAssert.isTrue(root.isArray() || root.isObject(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "上游数据必须为数组或对象，拒绝将无效响应视为空数据");
        List<JsonNode> result;
        if (root.isEmpty()) {
            return List.of();
        }
        if (dataset == WarframeCommunityDataset.REWARD_POOL
                && WarframeCommunitySource.PLUS_REWARDS.code().equals(sourceCode)
                && root.isObject()) {
            try {
                result = ExportRewardsParser.parse(root);
            } catch (IllegalArgumentException exception) {
                throw invalidFile(exception.getMessage());
            }
        } else if (dataset == WarframeCommunityDataset.RELIC
                || dataset == WarframeCommunityDataset.RELIC_REWARD
                || dataset == WarframeCommunityDataset.RELIC_DROP) {
            result = parseRelicRecords(dataset, root);
        } else if (dataset == WarframeCommunityDataset.RIVEN_UNROLLABLE) {
            result = parseRivenUnrollableRecords(root);
        } else if (dataset == WarframeCommunityDataset.MAP_FEATURE) {
            result = parseMapFeatureRecords(root, defaultWorld);
        } else if (dataset == WarframeCommunityDataset.WORLDSTATE_SORTIE) {
            result = parseSortieRecords(root);
        } else if (dataset == WarframeCommunityDataset.WORLDSTATE_RULE) {
            result = parseWorldStateRuleRecords(root);
        } else {
            result = new ArrayList<>();
            if (root.isArray()) {
                root.forEach(result::add);
            } else if (root.isObject()) {
                JsonNode array = firstArray(root, "data", "items", "records", "result");
                if (array != null) {
                    array.forEach(result::add);
                } else if (dataset == WarframeCommunityDataset.TRANSLATION
                        && isTranslationDictionary(root, sourceCode)) {
                    appendTranslationDictionaryRecords(result, root, sourceCode);
                } else if (root.path("data").isObject()
                        && allObjectValues(root.path("data"))) {
                    appendObjectMapRecords(result, root.path("data"),
                            text(root, "locale"));
                } else {
                    if (looksLikeSingleRecord(root)) {
                        result.add(root);
                    } else if (root.fields().hasNext()
                            && allObjectValues(root)) {
                        appendObjectMapRecords(result, root, text(root, "locale"));
                    } else {
                        throw invalidFile(dataset.fileName()
                                + "：无法识别对象格式，拒绝生成空数据集预览");
                    }
                }
            }
            if (isWorldStateDataset(dataset)) {
                result.forEach(record -> {
                    ObjectNode value = (ObjectNode) record;
                    if (!value.has("locale") || StrUtil.isBlank(value.path("locale").asText())) {
                        value.put("locale", worldStateProperties.getLocale());
                    }
                });
            }
        }
        if (result.stream().anyMatch(record -> !record.isObject())) {
            throw invalidFile(dataset.fileName() + "：记录必须是 JSON 对象");
        }
        if (dataset == WarframeCommunityDataset.BOUNTY_DEFINITION
                || dataset == WarframeCommunityDataset.TRANSLATION
                || dataset == WarframeCommunityDataset.REWARD_POOL) {
            result.forEach(record -> validateDatasetJson(dataset, (ObjectNode) record));
        }
        return result;
    }

    private void appendObjectMapRecords(
            List<JsonNode> result, JsonNode objectMap, String defaultLocale) {
        objectMap.fields().forEachRemaining(entry -> {
            ObjectNode value = (ObjectNode) entry.getValue().deepCopy();
            value.put("recordKey", entry.getKey());
            if (StrUtil.isNotBlank(defaultLocale) && !value.has("locale")) {
                value.put("locale", defaultLocale);
            }
            result.add(value);
        });
    }

    /**
     * plus 的 dict.zh/dict.en 是语言键到字符串的字典，不是
     * translation.json 的对象记录。保留语言来源和 locale，避免同一键的中英文
     * 在刷新身份计算时互相覆盖。
     */
    private void appendTranslationDictionaryRecords(
            List<JsonNode> result, JsonNode dictionary, String sourceCode) {
        String locale = sourceCode != null && sourceCode.endsWith("dict.en.json")
                ? "en" : "zh";
        dictionary.fields().forEachRemaining(entry -> {
            JsonNode displayName = entry.getValue();
            if (!displayName.isTextual() || StrUtil.isBlank(displayName.asText())) {
                log.warn("跳过无效翻译字典项, sourceCode={}, recordKey={}, valueType={}",
                        sourceCode, entry.getKey(), displayName == null
                                ? "null" : displayName.getNodeType());
                return;
            }
            ObjectNode value = objectMapper.createObjectNode();
            value.put("recordKey", entry.getKey());
            value.put("locale", locale);
            value.put("displayName", displayName.asText());
            result.add(value);
        });
    }

    /**
     * info-api 的规则文件是扁平数组，不是 conclaveData 使用的 key/value 分区结构。
     * 写入时统一补齐 WorldState 规则表需要的稳定键、业务数据集和语言维度，
     * 同时保留原记录中的全部字段，避免运行时只能看到投影字段。
     */
    private List<JsonNode> parseWorldStateRuleRecords(JsonNode root) {
        List<JsonNode> sourceRecords = new ArrayList<>();
        if (root.isArray()) {
            root.forEach(sourceRecords::add);
        } else {
            JsonNode array = firstArray(root, "data", "items", "records", "result");
            if (array != null) {
                array.forEach(sourceRecords::add);
            } else if (root.fields().hasNext() && allObjectValues(root)) {
                root.fields().forEachRemaining(entry -> {
                    ObjectNode value = (ObjectNode) entry.getValue().deepCopy();
                    if (!value.has("uniqueName")) {
                        value.put("uniqueName", entry.getKey());
                    }
                    sourceRecords.add(value);
                });
            } else {
                throw invalidFile("state_translation_local.json：无法识别规则数组");
            }
        }
        List<JsonNode> result = new ArrayList<>(sourceRecords.size());
        for (JsonNode sourceRecord : sourceRecords) {
            if (!sourceRecord.isObject()) {
                throw invalidFile("state_translation_local.json：规则记录必须是 JSON 对象");
            }
            ObjectNode value = (ObjectNode) sourceRecord.deepCopy();
            String uniqueName = text(value, "uniqueName", "recordKey");
            if (StrUtil.isBlank(uniqueName)) {
                throw invalidFile("state_translation_local.json：规则记录缺少 uniqueName");
            }
            value.put("uniqueName", uniqueName);
            value.put("recordKey", uniqueName);
            value.put("datasetCode", WarframeCommunityDataset.WORLDSTATE_RULE.code());
            if (StrUtil.isBlank(text(value, "locale"))) {
                value.put("locale", "zh");
            }
            result.add(value);
        }
        return result;
    }

    private boolean isWorldStateDataset(WarframeCommunityDataset dataset) {
        return switch (dataset) {
            case WORLDSTATE_MISSION_TYPE, WORLDSTATE_NODE, WORLDSTATE_FACTION,
                    WORLDSTATE_SYNDICATE, WORLDSTATE_OPERATION_TYPE, WORLDSTATE_UPGRADE_TYPE,
                    WORLDSTATE_PERSISTENT_ENEMY, WORLDSTATE_FISSURE_MODIFIER,
                    WORLDSTATE_SORTIE,
                    WORLDSTATE_LANGUAGE -> true;
            default -> false;
        };
    }

    private List<JsonNode> parseRelicRecords(
            WarframeCommunityDataset dataset, JsonNode root) {
        if (!root.isArray() && !root.isObject()) {
            throw invalidFile(dataset.fileName() + "：遗物数据必须是数组或对象");
        }
        List<JsonNode> result = new ArrayList<>();
        Iterable<Map.Entry<String, JsonNode>> relicEntries;
        if (root.isArray()) {
            List<Map.Entry<String, JsonNode>> entries = new ArrayList<>(root.size());
            for (int index = 0; index < root.size(); index++) {
                entries.add(Map.entry(String.valueOf(index), root.get(index)));
            }
            relicEntries = entries;
        } else if (root.has("rewards") || root.has("locations")) {
            relicEntries = List.of(Map.entry("", root));
        } else {
            List<Map.Entry<String, JsonNode>> entries = new ArrayList<>();
            root.fields().forEachRemaining(entries::add);
            relicEntries = entries;
        }
        for (Map.Entry<String, JsonNode> entry : relicEntries) {
            JsonNode relic = entry.getValue();
            if (!relic.isObject()) {
                throw invalidFile(dataset.fileName() + "：遗物记录必须是对象");
            }
            String relicKey = StrUtil.blankToDefault(
                    text(relic, "uniqueName", "name"),
                    StrUtil.blankToDefault(entry.getKey(), hash(writeJson(relic))));
            if (dataset == WarframeCommunityDataset.RELIC) {
                ObjectNode record = withRecordKey(relic, relicKey);
                if (StrUtil.isBlank(text(record, "name"))) {
                    record.put("name", relicKey);
                }
                if (StrUtil.isBlank(text(record, "uniqueName"))) {
                    record.put("uniqueName", relicKey);
                }
                result.add(record);
                continue;
            }
            JsonNode children = relic.get(dataset == WarframeCommunityDataset.RELIC_REWARD
                    ? "rewards" : "locations");
            if (children == null || !children.isArray()) {
                throw invalidFile(dataset.fileName() + "：遗物记录缺少 "
                        + (dataset == WarframeCommunityDataset.RELIC_REWARD
                        ? "rewards" : "locations") + " 数组");
            }
            for (int index = 0; index < children.size(); index++) {
                JsonNode child = children.get(index);
                BusinessAssert.isTrue(child.isObject(),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                        "遗物明细必须是对象");
                ObjectNode record = objectMapper.createObjectNode();
                record.put("recordKey", relicKey + (dataset == WarframeCommunityDataset.RELIC_REWARD
                        ? "#reward:" : "#drop:") + index);
                record.put("relicRecordKey", relicKey);
                record.put("relicName", text(relic, "name"));
                record.put("rewardOrder", index);
                record.set("data", child.deepCopy());
                result.add(record);
            }
        }
        return result;
    }

    private Map<WarframeCommunityDataset, List<JsonNode>> parseRelicBundle(
            String json, String sourceCode) {
        JsonNode root = readJson(json);
        BusinessAssert.isTrue(root.isArray() || root.isObject(),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "遗物数据必须为数组或对象");
        Map<WarframeCommunityDataset, List<JsonNode>> result = new LinkedHashMap<>();
        result.put(WarframeCommunityDataset.RELIC,
                parseRelicRecords(WarframeCommunityDataset.RELIC, root));
        result.put(WarframeCommunityDataset.RELIC_REWARD,
                parseRelicRecords(WarframeCommunityDataset.RELIC_REWARD, root));
        result.put(WarframeCommunityDataset.RELIC_DROP,
                parseRelicRecords(WarframeCommunityDataset.RELIC_DROP, root));
        result.forEach((dataset, records) -> validateSourceRecords(dataset, sourceCode, records));
        return result;
    }

    private List<DatasetSource> configuredRelicSources() {
        List<DatasetSource> sources = configuredSources(WarframeCommunityDataset.RELIC);
        BusinessAssert.isTrue(sources.size() == 1
                        && WarframeCommunitySource.WARFRAME_RELIC_DATA.code()
                        .equals(sources.getFirst().getSourceCode()),
                WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "遗物只允许配置一份 warframe-relic-data 的 Relics.json");
        return sources;
    }

    private List<JsonNode> parseRivenUnrollableRecords(JsonNode root) {
        if (!root.isObject()) {
            throw invalidFile(WarframeCommunityDataset.RIVEN_UNROLLABLE.fileName()
                    + "：不可展开紫卡数据必须是对象");
        }
        List<JsonNode> result = new ArrayList<>();
        root.fields().forEachRemaining(entry -> {
            if (!entry.getValue().isArray()) {
                throw invalidFile(WarframeCommunityDataset.RIVEN_UNROLLABLE.fileName()
                        + "：对象值必须是属性数组");
            }
            for (JsonNode attribute : entry.getValue()) {
                if (!attribute.isTextual() || StrUtil.isBlank(attribute.asText())) {
                    throw invalidFile(WarframeCommunityDataset.RIVEN_UNROLLABLE.fileName()
                            + "：不可展开属性必须是非空字符串");
                }
                ObjectNode record = objectMapper.createObjectNode();
                String attributeCode = attribute.asText().trim();
                record.put("recordKey", entry.getKey() + "#" + attributeCode);
                record.put("weaponCode", entry.getKey());
                record.put("attributeCode", attributeCode);
                result.add(record);
            }
        });
        return result;
    }

    private List<JsonNode> parseMapFeatureRecords(JsonNode root, String defaultWorld) {
        List<JsonNode> features = new ArrayList<>();
        String world = StrUtil.blankToDefault(text(root, "world"), defaultWorld);
        if (root.isObject() && "FeatureCollection".equals(root.path("type").asText())) {
            JsonNode values = root.get("features");
            if (values == null || !values.isArray()) {
                throw invalidFile(WarframeCommunityDataset.MAP_FEATURE.fileName()
                        + "：GeoJSON FeatureCollection 必须包含 features 数组");
            }
            values.forEach(features::add);
        } else if (root.isObject() && "Feature".equals(root.path("type").asText())) {
            features.add(root);
        } else if (root.isArray()) {
            root.forEach(features::add);
        } else {
            throw invalidFile(WarframeCommunityDataset.MAP_FEATURE.fileName()
                    + "：地图数据必须是 GeoJSON Feature 或 FeatureCollection");
        }
        List<JsonNode> result = new ArrayList<>();
        for (int index = 0; index < features.size(); index++) {
            JsonNode feature = features.get(index);
            if (!feature.isObject() || !"Feature".equals(feature.path("type").asText())) {
                throw invalidFile(WarframeCommunityDataset.MAP_FEATURE.fileName()
                        + "：地图记录必须是 GeoJSON Feature");
            }
            ObjectNode record = (ObjectNode) feature.deepCopy();
            String recordWorld = StrUtil.blankToDefault(
                    text(record, "world"), StrUtil.blankToDefault(world, "unknown"));
            record.put("world", recordWorld);
            String featureName = text(record.path("properties"), "name");
            record.put("recordKey", recordWorld + "#" + StrUtil.blankToDefault(
                    featureName, "feature-" + index) + "#" + index);
            result.add(record);
        }
        return result;
    }

    private List<JsonNode> parseSortieRecords(JsonNode root) {
        if (!root.isObject()) {
            throw invalidFile(WarframeCommunityDataset.WORLDSTATE_SORTIE.fileName()
                    + "：根节点必须是对象");
        }
        JsonNode descriptions = root.get("modifierDescriptions");
        List<JsonNode> result = new ArrayList<>();
        appendSortieSection(result, root.get("modifierTypes"), descriptions,
                "MODIFIER", true);
        appendSortieSection(result, root.get("bosses"), null, "BOSS", false);
        if (result.isEmpty()) {
            throw invalidFile(WarframeCommunityDataset.WORLDSTATE_SORTIE.fileName()
                    + "：缺少 modifierTypes 或 bosses 对象");
        }
        return result;
    }

    private void appendSortieSection(
            List<JsonNode> result,
            JsonNode values,
            JsonNode descriptions,
            String recordType,
            boolean modifier) {
        if (values == null || values.isNull()) {
            return;
        }
        if (!values.isObject()) {
            throw invalidFile(WarframeCommunityDataset.WORLDSTATE_SORTIE.fileName()
                    + "：突击 " + recordType + " 节必须是对象");
        }
        values.fields().forEachRemaining(entry -> {
            ObjectNode record = objectMapper.createObjectNode();
            record.put("recordKey", entry.getKey());
            record.put("locale", worldStateProperties.getLocale());
            record.put("recordType", recordType);
            if (entry.getValue().isObject()) {
                record.setAll((ObjectNode) entry.getValue().deepCopy());
            } else {
                record.put("value", entry.getValue().asText());
            }
            if (modifier && descriptions != null && descriptions.isObject()) {
                JsonNode description = descriptions.get(entry.getKey());
                if (description != null && !description.isNull()) {
                    record.put("description", description.asText());
                }
            }
            result.add(record);
        });
    }

    private ObjectNode withRecordKey(JsonNode value, String recordKey) {
        ObjectNode result = (ObjectNode) value.deepCopy();
        result.put("recordKey", recordKey);
        return result;
    }

    private static boolean allObjectValues(JsonNode root) {
        var fields = root.fields();
        boolean hasValue = false;
        while (fields.hasNext()) {
            hasValue = true;
            if (!fields.next().getValue().isObject()) {
                return false;
            }
        }
        return hasValue;
    }

    private static boolean allTextValues(JsonNode root) {
        var fields = root.fields();
        boolean hasValue = false;
        while (fields.hasNext()) {
            hasValue = true;
            if (!fields.next().getValue().isTextual()) {
                return false;
            }
        }
        return hasValue;
    }

    private static boolean isTranslationDictionary(JsonNode root, String sourceCode) {
        if (!root.isObject()) {
            return false;
        }
        if (sourceCode != null
                && (sourceCode.endsWith("dict.zh.json") || sourceCode.endsWith("dict.en.json"))) {
            return true;
        }
        var fields = root.fields();
        boolean hasValue = false;
        while (fields.hasNext()) {
            hasValue = true;
            var entry = fields.next();
            if (!entry.getKey().startsWith("/")
                    || entry.getValue().isObject()
                    || entry.getValue().isArray()) {
                return false;
            }
        }
        return hasValue;
    }

    private static boolean looksLikeSingleRecord(JsonNode root) {
        return root.has("id") || root.has("uniqueName") || root.has("url_name")
                || root.has("name") || root.has("cn") || root.has("en")
                || root.has("input_alias");
    }

    private void validateRivenAlias(WarframeCommunityDataset dataset, ObjectNode json) {
        if (dataset != WarframeCommunityDataset.MARKET_RIVEN_TION_ALIAS) return;
        var error = WarframeManagementErrorCode.INVALID_IMPORT_FILE;
        String name = StrUtil.nullToEmpty(text(json, "cn")).trim();
        String rawCodes = StrUtil.nullToEmpty(text(json, "en")).trim();
        BusinessAssert.isTrue(!name.isBlank() && name.length() <= 200, error, "别名长度必须为 1 到 200");
        List<String> codes = Arrays.stream(rawCodes.split(",", -1)).map(String::trim).toList();
        BusinessAssert.isTrue(codes.size() <= 3 && !codes.contains("")
                && new HashSet<>(codes).size() == codes.size(), error, "请选择一到三个不同属性");
        Set<String> allowed = all(WarframeCommunityDataset.MARKET_RIVEN_TION).stream()
                .filter(WarframeCommunityDatasetRecord::isEnabled)
                .map(record -> readJson(record.getCurrentJson()))
                .filter(node -> !node.path("search_only").asBoolean(false))
                .map(node -> text(node, "url_name"))
                .filter(Objects::nonNull)
                .filter(code -> !Set.of("has", "none", "").contains(code))
                .collect(Collectors.toSet());
        BusinessAssert.isTrue(allowed.containsAll(codes), error, "属性不存在或不能用于组合词条");
        String normalized = String.join(",", codes);
        BusinessAssert.isTrue(normalized.length() <= 200, error, "组合属性编码过长");
        json.put("cn", name);
        json.put("en", normalized);
    }

    private void validateDatasetJson(WarframeCommunityDataset dataset, ObjectNode json) {
        if (dataset == WarframeCommunityDataset.TRANSLATION) {
            String key = text(json, "recordKey");
            String locale = text(json, "locale");
            JsonNode displayName = json.get("displayName");
            StringBuilder missing = new StringBuilder();
            if (StrUtil.isBlank(key) || key.length() > 500) {
                missing.append("recordKey");
            }
            if (!Set.of("zh", "en").contains(locale)) {
                appendMissingField(missing, "locale(zh/en)");
            }
            if (displayName == null || !displayName.isTextual()
                    || StrUtil.isBlank(displayName.asText())) {
                appendMissingField(missing, "displayName");
            }
            BusinessAssert.isTrue(missing.length() == 0,
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "翻译记录不完整，recordKey=" + StrUtil.blankToDefault(key, "<blank>")
                            + "，缺少或非法字段：" + missing);
        }
        if (dataset == WarframeCommunityDataset.REWARD_POOL) {
            try {
                ObjectNode normalized = ExportRewardsParser.normalizePool(json);
                json.setAll(normalized);
            } catch (IllegalArgumentException exception) {
                throw invalidFile(exception.getMessage());
            }
        }
        if (dataset == WarframeCommunityDataset.BOUNTY_DEFINITION) {
            BusinessAssert.isTrue(json.path("name").isTextual()
                            && !json.path("name").asText().isBlank()
                            && json.path("stages").isArray(),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "赏金定义必须包含名称词条和完整阶段数组");
            for (JsonNode stage : json.path("stages")) {
                BusinessAssert.isTrue(stage.isArray(),
                        WarframeManagementErrorCode.INVALID_IMPORT_FILE, "赏金阶段必须为选项数组");
                for (JsonNode option : stage) {
                    BusinessAssert.isTrue(option.isTextual() && !option.asText().isBlank()
                                    && option.asText().length() <= 500,
                            WarframeManagementErrorCode.INVALID_IMPORT_FILE, "赏金阶段选项必须为任务路径");
                }
            }
        }
        validateRivenAlias(dataset, json);
        if (dataset != WarframeCommunityDataset.MARKET_COMPONENT) {
            return;
        }
        var error = WarframeManagementErrorCode.INVALID_IMPORT_FILE;
        String alias = StrUtil.nullToEmpty(text(json, "input_alias", "alias", "name")).trim();
        String suffix = StrUtil.nullToEmpty(text(json, "market_suffix", "suffix")).trim();
        BusinessAssert.isTrue(!alias.isBlank() && alias.length() <= 200,
                error, "Market 组件输入后缀必须为 1 到 200 个字符");
        BusinessAssert.isTrue(!suffix.isBlank() && suffix.length() <= 200,
                error, "Market 组件英文后缀必须为 1 到 200 个字符");
        json.put("input_alias", alias);
        json.put("market_suffix", suffix);
    }

    private static void appendMissingField(StringBuilder missing, String field) {
        if (missing.length() > 0) {
            missing.append(", ");
        }
        missing.append(field);
    }

    private static WarframeCommunityRecordSource effectiveRecordSource(
            WarframeCommunityDataset dataset, WarframeCommunityRecordSource source) {
        return dataset == WarframeCommunityDataset.MARKET_COMPONENT
                ? WarframeCommunityRecordSource.SELF_MAINTAINED : source;
    }

    private String sourceCode(WarframeCommunityDataset dataset,
            WarframeCommunityRecordSource recordSource) {
        if (recordSource == WarframeCommunityRecordSource.SELF_MAINTAINED) {
            return "nnmbot:" + dataset.code();
        }
        return configuredSources(dataset).stream()
                .findFirst()
                .map(DatasetSource::getSourceCode)
                .map(source -> WarframeCommunitySource.parse(dataset.code(), source).code())
                .orElseThrow(() -> new BusinessException(
                        WarframeManagementErrorCode.COMMUNITY_DATA_SOURCE_UNAVAILABLE,
                        "未配置社区数据来源：" + dataset.code()));
    }

    private String recordKey(WarframeCommunityDataset dataset, JsonNode node) {
        String explicitKey = text(node, "recordKey");
        if (StrUtil.isNotBlank(explicitKey)) {
            return explicitKey;
        }
        String key;
        if (dataset == WarframeCommunityDataset.RIVEN_TREND
                || dataset == WarframeCommunityDataset.RIVEN_ANALYSE_TREND) {
            key = text(node, "id") + ":" + text(node, "isDate");
        } else if (dataset == WarframeCommunityDataset.MARKET_RIVEN_TION) {
            key = text(node, "ids", "id", "url_name");
        } else if (dataset == WarframeCommunityDataset.MARKET_COMPONENT) {
            ObjectNode component = node.isObject()
                    ? (ObjectNode) node : objectMapper.createObjectNode();
            validateDatasetJson(dataset, component);
            key = "alias:" + text(component, "input_alias");
        } else if (dataset == WarframeCommunityDataset.ARBITRATION_ESSENCE) {
            key = text(node, "node", "nodeName", "name");
        } else {
            key = text(node, "uniqueName", "id", "url_name", "name", "en", "cn");
        }
        if (StrUtil.isBlank(key)) {
            key = hash(writeJson(node));
        }
        return key;
    }

    private List<DatasetSource> configuredSources(WarframeCommunityDataset dataset) {
        Map<String, List<DatasetSource>> configured = apiProperties.getDatasets();
        return configured == null ? List.of()
                : configured.getOrDefault(dataset.code(), List.of());
    }

    private boolean hasRefreshSource(WarframeCommunityDataset dataset) {
        dataset = aggregateRoot(dataset);
        return !configuredSources(dataset).isEmpty();
    }


    private String readFile(MultipartFile file, String expectedName) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_IMPORT_FILE_BYTES) {
            throw invalidFile(expectedName);
        }
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "读取社区数据文件失败", exception);
        }
    }

    private JsonNode readJson(String json) {
        try {
            BusinessAssert.isTrue(StrUtil.isNotBlank(json),
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE, "社区 JSON 不得为空");
            return objectMapper.reader()
                    .with(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                    .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                    .readTree(json);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "社区 JSON 格式不合法", exception);
        }
    }

    private String writeJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node == null ? objectMapper.createObjectNode() : node);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("社区 JSON 序列化失败", exception);
        }
    }

    private ObjectNode readObject(String json) {
        JsonNode value = readJson(json);
        if (!value.isObject()) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "社区记录 JSON 必须是对象");
        }
        return (ObjectNode) value.deepCopy();
    }

    private static JsonNode firstArray(JsonNode root, String... fields) {
        for (String field : fields) {
            JsonNode value = root.get(field);
            if (value != null && value.isArray()) {
                return value;
            }
        }
        return null;
    }

    private static String text(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && StrUtil.isNotBlank(value.asText())) {
                return value.asText().trim();
            }
        }
        return null;
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 不可用", exception);
        }
    }

    private static WarframeCommunityDataset parseDataset(String value) {
        try {
            return WarframeCommunityDataset.parse(value);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    exception.getMessage(), exception);
        }
    }

    private static BusinessException invalidFile(String value) {
        return new BusinessException(WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                "社区数据文件无效：" + value);
    }

    private record SourceRecord(String sourceCode, JsonNode value) {
    }
}
