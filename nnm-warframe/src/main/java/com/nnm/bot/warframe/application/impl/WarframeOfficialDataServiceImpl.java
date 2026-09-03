package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.config.http.HttpClientProperties;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.retry.RetryBackoff;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.application.WarframeOfficialDetailService;
import com.nnm.bot.warframe.application.WarframeOfficialDataService;
import com.nnm.bot.warframe.constant.WarframeAliasSourceType;
import com.nnm.bot.warframe.constant.WarframeDataSourceType;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.domain.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.config.properties.WarframeOfficialDataProperties;
import com.nnm.bot.warframe.dto.WarframeOfficialDataChange;
import com.nnm.bot.warframe.dto.WarframeOfficialRefreshResult;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import com.nnm.bot.warframe.repository.WarframeOfficialExportRecordRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.XZInputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class WarframeOfficialDataServiceImpl implements WarframeOfficialDataService {

    @Resource
    private WarframeApiClient apiClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialExportRecordRepository recordRepository;
    @Resource
    private WarframeOfficialDataProperties officialDataProperties;
    @Resource
    private HttpClientProperties httpClientProperties;
    @Resource
    private WarframeOfficialDetailService detailService;

    @Override
    @Transactional
    public WarframeOfficialRefreshResult refresh(TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        try {
            Map<String, OfficialItem> imported = new HashMap<>();
            progress.report("DOWNLOADING_INDEX", "正在下载官方英文索引", 0, 2);
            String englishIndex = downloadIndex(apiClient.englishLanguage(), progress);
            progress.report("PARSING_INDEX", "正在解析官方英文索引", 1, 2);
            importLanguage(englishIndex, apiClient.englishLanguage(), imported, progress);
            if (!apiClient.englishLanguage().equalsIgnoreCase(apiClient.language())) {
                progress.report("DOWNLOADING_INDEX", "正在下载官方本地化索引", 1, 2);
                String localizedIndex = downloadIndex(apiClient.language(), progress);
                progress.report("PARSING_INDEX", "正在解析官方本地化索引", 2, 2);
                importLanguage(localizedIndex, apiClient.language(), imported, progress);
            }
            if (imported.isEmpty()) {
                throw new BusinessException(WarframeManagementErrorCode.OFFICIAL_DATA_EMPTY);
            }
            progress.report("PERSISTING", "正在写入官方 PublicExport 数据", 0, imported.size());
            WarframeOfficialRefreshResult result = persist(imported, progress);
            String completionMessage = result.jsonChanges().isEmpty()
                    ? "官方 PublicExport 数据已更新"
                    : "官方 PublicExport 数据已更新；发现 "
                    + result.jsonChanges().size() + " 条 JSON 结构变化，请查看任务详情";
            progress.complete(completionMessage);
            return result;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("Official PublicExport refresh failed", exception);
            throw new BusinessException(
                    WarframeManagementErrorCode.OFFICIAL_DATA_REFRESH_FAILED,
                    "官方 PublicExport 刷新失败", exception);
        }
    }

    private WarframeOfficialRefreshResult persist(
            Map<String, OfficialItem> imported, TaskProgressReporter progress) {
        List<WarframeOfficialExportRecord> current = recordRepository.findAll();
        Map<String, WarframeOfficialExportRecord> currentByIdentity = new HashMap<>();
        for (WarframeOfficialExportRecord record : current) {
            currentByIdentity.put(officialIdentity(
                    record.getExportType(), record.getUniqueName()), record);
        }
        int created = 0;
        int updated = 0;
        int disabled = 0;
        List<WarframeOfficialDataChange> jsonChanges = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<WarframeOfficialExportRecord> importedRecords = new ArrayList<>(imported.size());
        List<Long> inactiveRecordIds = new ArrayList<>();
        int persisted = 0;
        for (OfficialItem source : imported.values()) {
            String key = officialIdentity(source.exportType(), source.uniqueName());
            WarframeOfficialExportRecord record = currentByIdentity.get(key);
            if (record == null) {
                record = new WarframeOfficialExportRecord();
                record.setUniqueName(source.uniqueName());
                created++;
                jsonChanges.add(new WarframeOfficialDataChange(
                        source.uniqueName(),
                        source.localizedName(),
                        "ADDED",
                        List.of(),
                        List.of(),
                        List.of()));
            } else {
                updated++;
                JsonFieldChanges englishChanges = compareJson(
                        record.getRawJson(), source.rawEnglishJson(), "en");
                JsonFieldChanges localizedChanges = compareJson(
                        record.getLocalizedRawJson(), source.rawLocalizedJson(), "localized");
                JsonFieldChanges changes = englishChanges.merge(localizedChanges);
                if (!changes.isEmpty()) {
                    jsonChanges.add(new WarframeOfficialDataChange(
                            source.uniqueName(),
                            source.localizedName(),
                            "UPDATED",
                            changes.addedFields(),
                            changes.changedFields(),
                            changes.removedFields()));
                }
            }
            String englishName = StrUtil.blankToDefault(
                    source.englishName(), source.localizedName());
            englishName = StrUtil.blankToDefault(englishName, source.uniqueName());
            String localizedName = StrUtil.blankToDefault(source.localizedName(), englishName);
            record.setExportFile(source.exportFile());
            record.setExportType(source.exportType());
            record.setRecordType(source.recordType());
            record.setOfficialName(englishName);
            record.setLocalizedName(localizedName);
            record.setParentName(source.parentName());
            record.setProductCategory(source.productCategory());
            record.setDescription(source.description());
            record.setLongDescription(source.longDescription());
            record.setWikiSlug(StrUtil.isBlank(source.localizedName())
                    ? record.getWikiSlug() : localizedName.replace(" ", ""));
            record.setRawJson(StrUtil.blankToDefault(source.rawEnglishJson(), "{}"));
            record.setLocalizedRawJson(source.rawLocalizedJson());
            record.setExcludeFromCodex(source.bool("excludeFromCodex"));
            record.setEnabled(true);
            record = recordRepository.save(record);
            importedRecords.add(record);
            seen.add(key);
            progress.report("PERSISTING", "正在写入官方 PublicExport 数据",
                    ++persisted, imported.size());
        }
        for (WarframeOfficialExportRecord record : current) {
            String key = officialIdentity(record.getExportType(), record.getUniqueName());
            if (seen.contains(key)) {
                continue;
            }
            if (record.getId() != null) {
                inactiveRecordIds.add(record.getId());
            }
            if (!record.isEnabled()) {
                continue;
            }
            record.setEnabled(false);
            recordRepository.save(record);
            disabled++;
            jsonChanges.add(new WarframeOfficialDataChange(
                    record.getUniqueName(),
                    record.getLocalizedName(),
                    "REMOVED",
                    List.of(),
                    List.of(),
                    List.of()));
        }
        recordRepository.flush();
        detailService.synchronize(importedRecords, inactiveRecordIds);
        return new WarframeOfficialRefreshResult(
                created, updated, disabled, 0, 0, jsonChanges);
    }

    private static String officialIdentity(String exportType, String uniqueName) {
        return (StrUtil.blankToDefault(exportType, "") + ":"
                + StrUtil.blankToDefault(uniqueName, "")).toLowerCase(Locale.ROOT);
    }

    private void importLanguage(
            String index,
            String language,
            Map<String, OfficialItem> imported,
            TaskProgressReporter progress) throws IOException {
        Map<String, String> files = parseIndex(index);
        long total = files.keySet().stream()
                .filter(fileName -> fileName.startsWith("Export")
                        && !fileName.startsWith("ExportManifest"))
                .count();
        long fileCurrent = 0;
        for (Map.Entry<String, String> file : files.entrySet()) {
            String fileName = file.getKey();
            if (!fileName.startsWith("Export") || fileName.startsWith("ExportManifest")) {
                continue;
            }
            String exportType = exportType(fileName);
            JsonNode root = downloadJson(
                    manifestReference(file.getKey(), file.getValue()), language);
            progress.report("DOWNLOADING_EXPORT",
                    "正在下载 " + fileName, ++fileCurrent, total);
            var records = root == null ? null : root.fields();
            if (records == null) {
                continue;
            }
            while (records.hasNext()) {
                Map.Entry<String, JsonNode> recordGroup = records.next();
                JsonNode values = recordGroup.getValue();
                if (values == null || !values.isArray()) {
                    continue;
                }
                int indexInGroup = 0;
                for (JsonNode value : values) {
                    if (value == null || !value.isObject()) {
                        indexInGroup++;
                        continue;
                    }
                    String uniqueName = text(value, "uniqueName", "abilityUniqueName");
                    if (StrUtil.isBlank(uniqueName)) {
                        uniqueName = syntheticKey(exportType, recordGroup.getKey(), indexInGroup);
                    }
                    String resolvedUniqueName = uniqueName;
                    String key = (exportType + ":" + uniqueName).toLowerCase(Locale.ROOT);
                    OfficialItem currentItem = imported.computeIfAbsent(key,
                            ignored -> new OfficialItem(
                                    resolvedUniqueName, exportType, recordGroup.getKey(),
                                    file.getKey(), file.getValue()));
                    if (language.equalsIgnoreCase(apiClient.englishLanguage())) {
                        currentItem.withEnglish(value);
                    } else {
                        currentItem.withLocalized(value);
                    }
                    indexInGroup++;
                }
            }
        }
    }

    private String downloadIndex(String language, TaskProgressReporter progress) throws IOException {
        IOException lastFailure = null;
        int maxAttempts = Math.max(1, httpClientProperties.getMaxAttempts());
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                String content = downloadIndexOnce(language);
                writeIndexCache(language, content);
                return content;
            } catch (IOException exception) {
                lastFailure = exception;
                if (attempt == maxAttempts || !sleepBeforeRetry(attempt)) {
                    break;
                }
                log.warn("PublicExport index retrying language={}, attempt={}/{}: {}",
                        language, attempt, maxAttempts, exception.getMessage());
            }
        }
        String cached = readIndexCache(language);
        if (cached != null) {
            log.warn("Using cached PublicExport index after refresh failure, language={}, reason={}",
                    language, lastFailure == null ? "unknown" : lastFailure.getMessage());
            return cached;
        }
        throw lastFailure;
    }

    private String downloadIndexOnce(String language) throws IOException {
        Path cachePath = indexCachePath(language);
        Files.createDirectories(cachePath.getParent());
        Path compressedTarget = Files.createTempFile(
                cachePath.getParent(), cachePath.getFileName() + "-", ".compressed");
        try {
            var result = apiClient.downloadTo(
                    java.net.URI.create(apiClient.publicExportIndexUrl()
                            .replace("{language}", language)),
                    Map.of("Accept", "application/x-lzma", "Accept-Encoding", "identity"),
                    compressedTarget);
            if (result == null) {
                throw new IOException("PublicExport index download returned no result");
            }
            if (!result.isSuccess() || !Files.isRegularFile(compressedTarget)
                    || Files.size(compressedTarget) == 0) {
                throw new IOException("PublicExport index HTTP " + result.statusCode()
                        + ", bytes=" + result.bytes());
            }
            try (InputStream input = indexInputStream(compressedTarget);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(input, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (content.length() > 0) {
                        content.append('\n');
                    }
                    content.append(line);
                }
                if (content.isEmpty()) {
                    throw new IOException("PublicExport index is empty");
                }
                return content.toString();
            }
        } catch (CorruptedInputException exception) {
            throw new IOException("PublicExport index compressed data is corrupt", exception);
        } finally {
            try {
                Files.deleteIfExists(compressedTarget);
            } catch (IOException cleanupFailure) {
                log.debug("Could not remove compressed PublicExport index {}", compressedTarget,
                        cleanupFailure);
            }
        }
    }

    private InputStream indexInputStream(Path file) throws IOException {
        BufferedInputStream input = new BufferedInputStream(Files.newInputStream(file));
        input.mark(13);
        byte[] header = input.readNBytes(13);
        input.reset();
        if (!isXz(header) && !isLzma(header)) {
            input.close();
            throw new IOException("PublicExport index returned an unsupported payload, bytes="
                    + Files.size(file));
        }
        // PublicExport 服务实际可能返回旧 .lzma 或 XZ；只看魔数选择容器，
        // 保留旧格式兼容性，避免固定使用 LZMAInputStream 解读 XZ 数据。
        if (isXz(header)) {
            return new XZInputStream(input);
        }
        LZMAInputStream stream = new LZMAInputStream(input, -1);
        // 官方索引当前可能没有 LZMA end marker，但压缩头中的未压缩长度和
        // 索引内容仍然完整。严格模式会把这种响应误判为损坏。
        stream.enableRelaxedEndCondition();
        return stream;
    }

    private Path indexCachePath(String language) {
        Path directory = officialDataProperties == null
                ? Path.of(System.getProperty("user.dir"), "data", "warframe", "public-export")
                : officialDataProperties.getCacheDirectory();
        return directory.toAbsolutePath().normalize().resolve("index_" + language + ".txt");
    }

    private void writeIndexCache(String language, String content) {
        Path target = indexCachePath(language);
        Path temporary = null;
        try {
            Files.createDirectories(target.getParent());
            temporary = Files.createTempFile(
                    target.getParent(), target.getFileName() + "-", ".tmp");
            Files.writeString(temporary, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            try (var channel = java.nio.channels.FileChannel.open(
                    temporary, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            log.warn("Could not cache PublicExport index {}", target, exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException cleanupFailure) {
                    log.debug("Could not remove temporary PublicExport index cache {}",
                            temporary, cleanupFailure);
                }
            }
        }
    }

    private String readIndexCache(String language) {
        Path target = indexCachePath(language);
        try {
            if (Files.isRegularFile(target) && Files.size(target) > 0) {
                return Files.readString(target, StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            log.warn("Could not read cached PublicExport index {}", target, exception);
        }
        return null;
    }

    private boolean isXz(byte[] body) {
        return body.length >= 6
                && (body[0] & 0xff) == 0xfd
                && (body[1] & 0xff) == 0x37
                && (body[2] & 0xff) == 0x7a
                && (body[3] & 0xff) == 0x58
                && (body[4] & 0xff) == 0x5a
                && (body[5] & 0xff) == 0x00;
    }

    private boolean isLzma(byte[] body) {
        if (body.length < 13) {
            return false;
        }
        int properties = body[0] & 0xff;
        long dictionarySize = (body[1] & 0xffL)
                | ((body[2] & 0xffL) << 8)
                | ((body[3] & 0xffL) << 16)
                | ((body[4] & 0xffL) << 24);
        return properties <= 224 && dictionarySize >= 4096;
    }

    private boolean sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(RetryBackoff.exponential(
                    httpClientProperties.getRetryBackoff(),
                    httpClientProperties.getRetryMaxBackoff(),
                    attempt).toMillis());
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private JsonNode downloadJson(String fileName, String language) throws IOException {
        var result = apiClient.getText(
                java.net.URI.create(apiClient.publicExportManifestUrl()
                        .replace("{file}", fileName)),
                Map.of("Accept", "application/json", "Accept-Language", language));
        if (!result.isSuccess() || StrUtil.isBlank(result.body())) {
            throw new IOException("PublicExport file HTTP " + result.statusCode());
        }
        try {
            return objectMapper.readTree(result.body());
        } catch (RuntimeException exception) {
            throw new IOException("PublicExport JSON cannot be parsed", exception);
        }
    }

    private static Map<String, String> parseIndex(String index) {
        Map<String, String> result = new HashMap<>();
        for (String line : index.split("\\R")) {
            String value = line.trim();
            int separator = value.indexOf('!');
            if (separator <= 0) {
                continue;
            }
            String fileName = value.substring(0, separator).trim();
            result.put(fileName, value.substring(separator + 1).trim());
        }
        return result;
    }

    private static String manifestReference(String fileName, String hash) {
        return fileName + "!" + hash;
    }

    private static String exportType(String fileName) {
        int separator = fileName.lastIndexOf('_');
        return separator > 0 ? fileName.substring(0, separator) : fileName;
    }

    private static String syntheticKey(String exportType, String recordType, int index) {
        return "/__public_export__/" + exportType + "/" + recordType + "/" + index;
    }

    private JsonFieldChanges compareJson(
            String previousJson, String currentJson, String prefix) {
        JsonNode previous = readJson(previousJson);
        JsonNode current = readJson(currentJson);
        if (previous == null && current == null) {
            return JsonFieldChanges.empty();
        }
        if (previous == null || current == null) {
            return new JsonFieldChanges(
                    List.of(),
                    List.of(prefix),
                    List.of());
        }
        JsonFieldChanges changes = compareJson(previous, current, prefix);
        return changes;
    }

    private JsonFieldChanges compareJson(JsonNode previous, JsonNode current, String path) {
        if (previous.equals(current)) {
            return JsonFieldChanges.empty();
        }
        if (!previous.isObject() || !current.isObject()) {
            return new JsonFieldChanges(List.of(), List.of(path), List.of());
        }
        List<String> added = new ArrayList<>();
        List<String> changed = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        Set<String> fields = new java.util.TreeSet<>();
        previous.fieldNames().forEachRemaining(fields::add);
        current.fieldNames().forEachRemaining(fields::add);
        for (String field : fields) {
            String fieldPath = path + "." + field;
            JsonNode oldValue = previous.get(field);
            JsonNode newValue = current.get(field);
            if (oldValue == null) {
                added.add(fieldPath);
            } else if (newValue == null) {
                removed.add(fieldPath);
            } else {
                JsonFieldChanges nested = compareJson(oldValue, newValue, fieldPath);
                added.addAll(nested.addedFields());
                changed.addAll(nested.changedFields());
                removed.addAll(nested.removedFields());
            }
        }
        return new JsonFieldChanges(added, changed, removed);
    }

    private JsonNode readJson(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return objectMapper.readTree(value);
        } catch (IOException exception) {
            return null;
        }
    }

    private record JsonFieldChanges(
            List<String> addedFields,
            List<String> changedFields,
            List<String> removedFields) {

        private JsonFieldChanges {
            addedFields = List.copyOf(addedFields);
            changedFields = List.copyOf(changedFields);
            removedFields = List.copyOf(removedFields);
        }

        private static JsonFieldChanges empty() {
            return new JsonFieldChanges(List.of(), List.of(), List.of());
        }

        private boolean isEmpty() {
            return addedFields.isEmpty()
                    && changedFields.isEmpty()
                    && removedFields.isEmpty();
        }

        private JsonFieldChanges merge(JsonFieldChanges other) {
            List<String> added = new ArrayList<>(addedFields);
            added.addAll(other.addedFields());
            List<String> changed = new ArrayList<>(changedFields);
            changed.addAll(other.changedFields());
            List<String> removed = new ArrayList<>(removedFields);
            removed.addAll(other.removedFields());
            return new JsonFieldChanges(added, changed, removed);
        }
    }

    private static String text(JsonNode node, String... fields) {
        if (node == null || fields == null) {
            return null;
        }
        for (String field : fields) {
            if (StrUtil.isBlank(field)) {
                continue;
            }
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && !value.isMissingNode()) {
                String text = value.asText();
                if (StrUtil.isNotBlank(text)) {
                    return text;
                }
            }
        }
        return null;
    }

    private final class OfficialItem {

        private final String uniqueName;
        private final String exportType;
        private final String recordType;
        private final String exportFile;
        private String englishName;
        private String localizedName;
        private String parentName;
        private String productCategory;
        private String description;
        private String longDescription;
        private String rawEnglishJson;
        private String rawLocalizedJson;
        private JsonNode englishNode;
        private JsonNode localizedNode;

        private OfficialItem(
                String uniqueName,
                String exportType,
                String recordType,
                String exportFile,
                String ignoredHash) {
            this.uniqueName = uniqueName;
            this.exportType = exportType;
            this.recordType = recordType;
            this.exportFile = exportFile;
        }

        private void withEnglish(JsonNode node) throws IOException {
            this.englishName = WarframeOfficialDataServiceImpl.text(node, "name");
            this.englishNode = node;
            this.rawEnglishJson = objectMapper.writeValueAsString(node);
            this.copyCommonFields(node);
        }

        private void withLocalized(JsonNode node) throws IOException {
            this.localizedName = WarframeOfficialDataServiceImpl.text(node, "name");
            this.localizedNode = node;
            this.rawLocalizedJson = objectMapper.writeValueAsString(node);
            // Common structural fields are language-independent in practice, but
            // taking them from the localized file preserves localized descriptions.
            this.copyCommonFields(node);
        }

        private void copyCommonFields(JsonNode node) {
            this.parentName = WarframeOfficialDataServiceImpl.text(node, "parentName");
            this.productCategory = WarframeOfficialDataServiceImpl.text(node, "productCategory");
            this.description = jsonText(node, "description");
            this.longDescription = jsonText(node, "longDescription");
        }

        private String uniqueName() {
            return uniqueName;
        }

        private String exportType() {
            return exportType;
        }

        private String recordType() {
            return recordType;
        }

        private String exportFile() {
            return exportFile;
        }

        private String englishName() {
            return englishName;
        }

        private String localizedName() {
            return localizedName;
        }

        private String parentName() {
            return parentName;
        }

        private String productCategory() {
            return productCategory;
        }

        private String description() {
            return description;
        }

        private String longDescription() {
            return longDescription;
        }

        private String text(String... fields) {
            JsonNode value = firstValue(fields);
            return value == null || value.isNull() || !value.isValueNode()
                    ? null : value.asText();
        }

        private String rawEnglishJson() {
            return rawEnglishJson;
        }

        private String rawLocalizedJson() {
            return rawLocalizedJson;
        }

        private Integer integer(String... fields) {
            JsonNode value = firstValue(fields);
            return value != null && value.canConvertToInt() ? value.intValue() : null;
        }

        private Double decimal(String... fields) {
            JsonNode value = firstValue(fields);
            return value != null && value.isNumber() ? value.doubleValue() : null;
        }

        private Boolean bool(String... fields) {
            JsonNode value = firstValue(fields);
            return value != null && value.isBoolean() ? value.booleanValue() : null;
        }

        private String json(String... fields) {
            JsonNode value = firstValue(fields);
            return value == null || value.isNull() ? null : value.toString();
        }

        private JsonNode firstValue(String... fields) {
            JsonNode englishValue = firstValue(englishNode, fields);
            if (englishValue != null) {
                return englishValue;
            }
            return firstValue(localizedNode, fields);
        }

        private JsonNode firstValue(JsonNode node, String... fields) {
            if (node == null) {
                return null;
            }
            for (String field : fields) {
                JsonNode value = node.get(field);
                if (value != null && !value.isNull()) {
                    return value;
                }
            }
            return null;
        }
    }

    private static String jsonText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.isTextual() ? value.asText() : value.toString();
    }
}
