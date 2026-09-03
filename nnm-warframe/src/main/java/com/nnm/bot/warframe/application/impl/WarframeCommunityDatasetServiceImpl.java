package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.application.CommunityJsonMergeEngine;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetProjectionMapper;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetService;
import com.nnm.bot.warframe.config.properties.WarframeApiProperties;
import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetBatchRefreshResult;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetRefreshSummary;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetRecordMergeRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityFieldDecision;
import com.nnm.bot.warframe.dto.WarframeCommunityFieldDiff;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

/**
 * 社区仓库数据集服务。
 *
 * <p>刷新只自动落库无冲突变化，并把冲突保留在记录的 upstreamJson 中；
 * 这样数据库永远能看到最新上游内容，同时不会悄悄覆盖管理员改动。</p>
 */
@Service
public class WarframeCommunityDatasetServiceImpl implements WarframeCommunityDatasetService {

    private static final long MAX_IMPORT_FILE_BYTES = 8L * 1024 * 1024;
    private static final int MAX_PAGE_SIZE = 200;

    @PersistenceContext
    private EntityManager entityManager;
    @jakarta.annotation.Resource
    private ObjectMapper objectMapper;
    @jakarta.annotation.Resource
    private WarframeApiProperties apiProperties;
    @jakarta.annotation.Resource
    private WarframeApiClient apiClient;
    @jakarta.annotation.Resource
    private WarframeCommunityDatasetProjectionMapper projectionMapper;

    @Override
    public List<WarframeCommunityDatasetInfo> datasets() {
        return List.of(WarframeCommunityDataset.values()).stream()
                .map(dataset -> {
                    List<WarframeCommunityDatasetRecord> records = all(dataset);
                    return new WarframeCommunityDatasetInfo(
                            dataset.code(), dataset.fileName(), dataset.label(),
                            records.size(), records.stream()
                                    .filter(WarframeCommunityDatasetRecord::isEnabled).count());
                })
                .toList();
    }

    @Override
    public PageResult<WarframeCommunityDatasetItem> query(
            String datasetCode, WarframeCommunityDatasetQueryRequest request) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
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
        int from = Math.min((current - 1) * size, matched.size());
        int to = Math.min(from + size, matched.size());
        return PageResult.of(matched.subList(from, to), current, size, matched.size());
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetPreview refresh(String datasetCode) {
        return refresh(datasetCode, TaskProgressReporter.noop());
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetPreview refresh(
            String datasetCode, TaskProgressReporter reporter) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        String source = firstSource(dataset);
        progress.report("DOWNLOADING", "正在拉取" + dataset.label(), 0, 1);
        String json = fetch(source, configuredUrls(dataset));
        progress.report("RECONCILING", "正在合并" + dataset.label(), 0, 1);
        WarframeCommunityDatasetPreview preview =
                reconcile(dataset, source, parseRecords(dataset, json), true);
        progress.complete(dataset.label() + "刷新完成");
        return preview;
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetBatchRefreshResult refreshAll(
            TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        WarframeCommunityDataset[] datasets = WarframeCommunityDataset.values();
        List<WarframeCommunityDatasetRefreshSummary> summaries =
                new ArrayList<>(datasets.length);
        for (int index = 0; index < datasets.length; index++) {
            WarframeCommunityDataset dataset = datasets[index];
            progress.report(
                    "DATASET",
                    "正在刷新" + dataset.label(),
                    index,
                    datasets.length);
            WarframeCommunityDatasetPreview preview =
                    refresh(dataset.code(), TaskProgressReporter.noop());
            summaries.add(new WarframeCommunityDatasetRefreshSummary(
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
                    datasets.length);
        }
        return new WarframeCommunityDatasetBatchRefreshResult(
                datasets.length, summaries.size(), summaries);
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetItem createSelfMaintained(
            String datasetCode, String currentJson) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        ObjectNode json = readObject(currentJson);
        String key = recordKey(dataset, json);
        String normalizedKey = normalizeKey(key);
        boolean exists = all(dataset).stream()
                .anyMatch(record -> normalizeKey(record.getRecordKey()).equals(normalizedKey));
        if (exists) {
            throw new BusinessException(WarframeManagementErrorCode.SOURCE_KEY_DUPLICATED);
        }
        WarframeCommunityDatasetRecord record = newRecord(
                dataset, key, json, WarframeCommunityRecordSource.SELF_MAINTAINED);
        entityManager.persist(record);
        return toItem(dataset, record);
    }

    @Override
    public WarframeCommunityDatasetPreview uploadPreview(
            String datasetCode, MultipartFile file) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        String json = readFile(file, dataset.fileName());
        return reconcile(dataset, "upload://"
                + StrUtil.blankToDefault(file.getOriginalFilename(), dataset.fileName()),
                parseRecords(dataset, json), false);
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetPreview merge(
            String datasetCode, WarframeCommunityDatasetMergeRequest request) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        Map<String, WarframeCommunityDatasetRecordMergeRequest> requested =
                request.records().stream().collect(Collectors.toMap(
                        item -> normalizeKey(item.recordKey()),
                        item -> item, (first, ignored) -> first, LinkedHashMap::new));
        List<WarframeCommunityDatasetRecord> records = all(dataset);
        List<WarframeCommunityFieldDiff> diffs = new ArrayList<>();
        int updated = 0;
        int added = 0;
        for (WarframeCommunityDatasetRecord record : records) {
            WarframeCommunityDatasetRecordMergeRequest item = requested.get(
                    normalizeKey(record.getRecordKey()));
            if (item == null) {
                continue;
            }
            if (isSelfMaintained(record)) {
                // 自维护记录与社区上游同键时仍以管理员维护版本为准。
                continue;
            }
            List<WarframeCommunityFieldDecision> decisions = item.decisions() == null
                    ? List.of() : item.decisions();
            if (Boolean.FALSE.equals(item.upstreamPresent())) {
                boolean removeRecord = decisions.stream()
                        .anyMatch(decision -> "__record__".equals(decision.field())
                                && "USE_UPSTREAM".equalsIgnoreCase(decision.decision()));
                boolean keepManual = record.isManualModified() && !removeRecord;
                String emptyUpstream = writeJson(objectMapper.createObjectNode());
                boolean recordChanged = record.isEnabled() != keepManual
                        || !keepManual && record.isManualModified()
                        || !emptyUpstream.equals(record.getUpstreamJson());
                if (removeRecord || !record.isManualModified()) {
                    // 上游已删除时保留数据库记录用于审计，但不再参与运行时查询。
                    record.setCurrentJson(emptyUpstream);
                    projectionMapper.sync(record, objectMapper.createObjectNode());
                    record.setManualFieldsJson(emptyUpstream);
                    record.setManualModified(false);
                    record.setEnabled(false);
                } else {
                    // 人工记录默认继续生效，管理员可通过 __record__ 决策将其停用。
                    record.setEnabled(true);
                }
                record.setUpstreamJson(emptyUpstream);
                record.setUpstreamHash(hash(emptyUpstream));
                record.setSourceHash(record.getUpstreamHash());
                if (recordChanged || !record.isEnabled()) {
                    updated++;
                }
                entityManager.merge(record);
                continue;
            }
            JsonNode upstream = readJson(item.upstreamJson());
            CommunityJsonMergeEngine.MergeResult result = mergeEngine().merge(
                    dataset.code(), record.getRecordKey(), readJson(record.getCurrentJson()),
                    upstream, readJson(record.getManualFieldsJson()));
            Set<String> useUpstream = decisions.stream()
                    .filter(decision -> "USE_UPSTREAM".equalsIgnoreCase(decision.decision()))
                    .map(WarframeCommunityFieldDecision::field)
                    .collect(Collectors.toSet());
            CommunityJsonMergeEngine.DecisionResult decisionResult = mergeEngine().applyDecision(
                    result.merged(), upstream, result.manualFields(), result.diffs(), useUpstream);
            ObjectNode manual = decisionResult.manualFields();
            String currentJson = writeJson(decisionResult.merged());
            if (!currentJson.equals(record.getCurrentJson())
                    || !writeJson(manual).equals(record.getManualFieldsJson())
                    || !item.upstreamJson().equals(record.getUpstreamJson())) {
                updated++;
            }
            record.setCurrentJson(currentJson);
            projectionMapper.sync(record, decisionResult.merged());
            record.setUpstreamJson(writeJson(upstream));
            record.setUpstreamHash(hash(record.getUpstreamJson()));
            record.setSourceHash(record.getUpstreamHash());
            record.setManualFieldsJson(writeJson(manual));
            record.setManualModified(!manual.isEmpty());
            record.setEnabled(true);
            entityManager.merge(record);
            diffs.addAll(result.diffs());
        }
        for (WarframeCommunityDatasetRecordMergeRequest item : requested.values()) {
            String normalizedKey = item.recordKey().trim().toLowerCase(Locale.ROOT);
            boolean exists = records.stream()
                    .anyMatch(record -> record.getRecordKey().trim()
                            .toLowerCase(Locale.ROOT).equals(normalizedKey));
            if (exists) {
                continue;
            }
            // 上传预览中的新增记录在审批通过后才首次写入数据库。
            WarframeCommunityDatasetRecord created = newRecord(
                    dataset, item.recordKey().trim(), readJson(item.upstreamJson()),
                    WarframeCommunityRecordSource.COMMUNITY);
            entityManager.persist(created);
            records.add(created);
            added++;
        }
        return new WarframeCommunityDatasetPreview(
                dataset.code(), "merge", records.size(), added, updated,
                (int) diffs.stream().filter(WarframeCommunityFieldDiff::conflict).count(),
                diffs, records.stream().map(record -> toItem(dataset, record)).toList());
    }

    @Override
    @Transactional
    public WarframeCommunityDatasetItem updateManual(
            String datasetCode, long id, String currentJson) {
        WarframeCommunityDataset dataset = parseDataset(datasetCode);
        WarframeCommunityDatasetRecord record = entityManager.find(dataset.entityType(), id);
        if (record == null) {
            throw new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND);
        }
        ObjectNode before = readObject(record.getCurrentJson());
        ObjectNode after = readObject(currentJson);
        if (isSelfMaintained(record)) {
            String json = writeJson(after);
            record.setCurrentJson(json);
            projectionMapper.sync(record, after);
            record.setManualFieldsJson("{}");
            record.setManualModified(false);
            record.setSourceHash(hash(json));
            entityManager.merge(record);
            return toItem(dataset, record);
        }
        ObjectNode manual = readObject(record.getManualFieldsJson());
        ObjectNode upstream = readObject(record.getUpstreamJson());
        Set<String> fields = new LinkedHashSet<>();
        before.fieldNames().forEachRemaining(fields::add);
        after.fieldNames().forEachRemaining(fields::add);
        for (String field : fields) {
            JsonNode oldValue = before.get(field);
            JsonNode newValue = after.get(field);
            if (!same(oldValue, newValue)) {
                JsonNode upstreamValue = upstream.get(field);
                if (same(newValue, upstreamValue)) {
                    manual.remove(field);
                } else {
                    manual.set(field, newValue == null
                            ? NullNode.getInstance() : newValue.deepCopy());
                }
            }
        }
        record.setCurrentJson(writeJson(after));
        projectionMapper.sync(record, after);
        record.setManualFieldsJson(writeJson(manual));
        record.setManualModified(!manual.isEmpty());
        entityManager.merge(record);
        return toItem(dataset, record);
    }

    private WarframeCommunityDatasetPreview reconcile(
            WarframeCommunityDataset dataset,
            String source,
            List<JsonNode> incoming,
            boolean persist) {
        Map<String, WarframeCommunityDatasetRecord> existing = all(dataset).stream()
                .collect(Collectors.toMap(record -> normalizeKey(record.getRecordKey()),
                        record -> record, (first, ignored) -> first, LinkedHashMap::new));
        // 上游文件偶尔会重复导出同一条记录，先去重再进入 JPA，避免同事务重复 persist。
        Map<String, JsonNode> uniqueIncoming = new LinkedHashMap<>();
        for (JsonNode upstream : incoming) {
            String key = recordKey(dataset, upstream);
            uniqueIncoming.putIfAbsent(normalizeKey(key), upstream);
        }
        List<WarframeCommunityFieldDiff> diffs = new ArrayList<>();
        List<WarframeCommunityDatasetItem> previewRecords = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int added = 0;
        int changed = 0;
        for (Map.Entry<String, JsonNode> entry : uniqueIncoming.entrySet()) {
            JsonNode upstream = entry.getValue();
            String key = recordKey(dataset, upstream);
            String normalizedKey = entry.getKey();
            seen.add(normalizedKey);
            WarframeCommunityDatasetRecord current = existing.get(normalizedKey);
            if (current == null) {
                WarframeCommunityDatasetRecord created = newRecord(
                        dataset, key, upstream, WarframeCommunityRecordSource.COMMUNITY);
                added++;
                if (persist) {
                    entityManager.persist(created);
                }
                previewRecords.add(toItem(dataset, created));
                continue;
            }
            if (isSelfMaintained(current)) {
                previewRecords.add(toItem(dataset, current));
                continue;
            }
            CommunityJsonMergeEngine.MergeResult result = mergeEngine().merge(
                    dataset.code(), key, readJson(current.getCurrentJson()), upstream,
                    readJson(current.getManualFieldsJson()));
            diffs.addAll(result.diffs());
            String mergedJson = writeJson(result.merged());
            String upstreamJson = writeJson(upstream);
            // 合并按字段执行：冲突字段保留人工值，其余字段仍应立即跟随上游。
            if (!mergedJson.equals(current.getCurrentJson())) {
                changed++;
            }
            current.setCurrentJson(mergedJson);
            projectionMapper.sync(current, result.merged());
            current.setManualModified(!result.manualFields().isEmpty());
            current.setManualFieldsJson(writeJson(result.manualFields()));
            current.setUpstreamJson(upstreamJson);
            current.setUpstreamHash(hash(upstreamJson));
            current.setSourceHash(current.getUpstreamHash());
            current.setEnabled(true);
            if (persist) {
                entityManager.merge(current);
            }
            previewRecords.add(toItem(dataset, current));
        }
        for (WarframeCommunityDatasetRecord record : existing.values()) {
            if (seen.contains(normalizeKey(record.getRecordKey()))
                    || isSelfMaintained(record)) {
                continue;
            }
            if (!record.isManualModified()) {
                record.setEnabled(false);
                if (persist) {
                    entityManager.merge(record);
                }
                // 缺失记录也进入预览，上传确认时才能执行停用；刷新场景则直接返回停用后的状态。
                previewRecords.add(toItem(dataset, record, false, false));
            } else {
                // 人工记录的删除属于冲突，默认保留，管理员可在预览中选择停用。
                diffs.add(new WarframeCommunityFieldDiff(
                        dataset.code(), record.getRecordKey(), "__record__",
                        true, false, true, true, "KEEP_MANUAL"));
                previewRecords.add(toItem(dataset, record, false, true));
            }
        }
        return new WarframeCommunityDatasetPreview(
                dataset.code(), source, uniqueIncoming.size(), added, changed,
                (int) diffs.stream().filter(WarframeCommunityFieldDiff::conflict).count(),
                diffs, previewRecords);
    }

    private WarframeCommunityDatasetRecord newRecord(
            WarframeCommunityDataset dataset,
            String key,
            JsonNode upstream,
            WarframeCommunityRecordSource recordSource) {
        WarframeCommunityDatasetRecord record;
        try {
            record = dataset.entityType().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("无法创建社区数据集记录：" + dataset.code(), exception);
        }
        String json = writeJson(upstream);
        String upstreamJson = recordSource == WarframeCommunityRecordSource.COMMUNITY
                ? json : "{}";
        record.setRecordKey(key);
        record.setRecordSource(recordSource);
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

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset, WarframeCommunityDatasetRecord record) {
        return toItem(dataset, record, true, record.isEnabled());
    }

    private WarframeCommunityDatasetItem toItem(
            WarframeCommunityDataset dataset,
            WarframeCommunityDatasetRecord record,
            boolean upstreamPresent,
            boolean enabled) {
        String upstreamJson = upstreamPresent ? record.getUpstreamJson() : "{}";
        return new WarframeCommunityDatasetItem(
                record.getId(), dataset.code(), record.getRecordKey(),
                record.getRecordSource(), projectionMapper.details(record),
                record.getCurrentJson(), upstreamJson, record.getOriginalJson(),
                record.getManualFieldsJson(), record.isManualModified(), enabled,
                upstreamPresent,
                record.getCreatedAt(), record.getUpdatedAt());
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
        return StrUtil.blankToDefault(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private List<WarframeCommunityDatasetRecord> all(WarframeCommunityDataset dataset) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        List<WarframeCommunityDatasetRecord> records = (List) entityManager.createQuery(
                "select e from " + dataset.entityType().getSimpleName() + " e").getResultList();
        return new ArrayList<>(records);
    }

    private List<JsonNode> parseRecords(
            WarframeCommunityDataset dataset, String json) {
        JsonNode root = readJson(json);
        List<JsonNode> result = new ArrayList<>();
        if (root.isArray()) {
            root.forEach(result::add);
        } else if (root.isObject()) {
            JsonNode array = firstArray(root, "data", "items", "records", "result");
            if (array != null) {
                array.forEach(result::add);
            } else {
                root.fields().forEachRemaining(entry -> {
                    if (entry.getValue().isObject()) {
                        ObjectNode value = (ObjectNode) entry.getValue().deepCopy();
                        if (!value.has("id") && !value.has("uniqueName")) {
                            value.put("id", entry.getKey());
                        }
                        result.add(value);
                    }
                });
            }
        }
        if (result.isEmpty() && !root.isArray() && !root.isObject()) {
            throw invalidFile(dataset.fileName());
        }
        return result;
    }

    private String recordKey(WarframeCommunityDataset dataset, JsonNode node) {
        String key;
        if (dataset == WarframeCommunityDataset.RIVEN_TREND
                || dataset == WarframeCommunityDataset.RIVEN_ANALYSE_TREND) {
            key = text(node, "id") + ":" + text(node, "isDate");
        } else if (dataset == WarframeCommunityDataset.MARKET_RIVEN_TION) {
            key = text(node, "ids", "id", "url_name");
        } else {
            key = text(node, "uniqueName", "id", "url_name", "name", "en", "cn");
        }
        if (StrUtil.isBlank(key)) {
            key = hash(writeJson(node));
        }
        return key;
    }

    private String firstSource(WarframeCommunityDataset dataset) {
        List<java.net.URI> urls = configuredUrls(dataset);
        if (urls.isEmpty()) {
            throw invalidFile(dataset.fileName());
        }
        return urls.getFirst().toString();
    }

    private List<java.net.URI> configuredUrls(WarframeCommunityDataset dataset) {
        Map<String, List<java.net.URI>> configured = apiProperties.getCommunityDatasetUrls();
        return configured == null ? List.of()
                : configured.getOrDefault(dataset.code(), List.of());
    }

    private String fetch(String source, List<java.net.URI> urls) {
        List<java.net.URI> candidates = urls == null || urls.isEmpty()
                ? List.of(java.net.URI.create(source)) : urls;
        for (java.net.URI url : candidates) {
            HttpClient.HttpResult result = apiClient.getText(
                    url, Map.of("Accept", "application/json"));
            if (result.isSuccess() && StrUtil.isNotBlank(result.body())) {
                return result.body();
            }
        }
        throw invalidFile(source);
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

    private CommunityJsonMergeEngine mergeEngine() {
        return new CommunityJsonMergeEngine(objectMapper);
    }

    private JsonNode readJson(String json) {
        try {
            return objectMapper.readTree(StrUtil.blankToDefault(json, "{}"));
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

    private static boolean same(JsonNode first, JsonNode second) {
        if (first == null || first.isNull()) {
            return second == null || second.isNull();
        }
        return first.equals(second);
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
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
}
