package com.nnm.bot.warframe.cache;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties;
import com.nnm.bot.warframe.constant.WarframeCommunityPreviewStatus;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewGroup;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewIndex;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewSnapshot;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewSnapshotRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityFieldDiff;
import io.github.rdt.cache.CacheEntry;
import io.github.rdt.cache.CacheService;
import io.github.rdt.support.JsonCodec;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** 社区预览的 Redis 存储边界；不参与来源比对、业务合并或数据库事务。 */
@Component
@Slf4j
public class WarframeCommunityPreviewStore {

    private static final String PREVIEW_PREFIX = "warframe-community-preview:";
    private static final String RECORD_PREFIX = "warframe-community-preview-chunk:";
    private static final String DIFF_PREFIX = "warframe-community-preview-diff-chunk:";
    private static final String GROUP_PREFIX = "warframe-community-preview-group:";
    private static final String CONSUMED_PREFIX = "warframe-community-preview-consumed:";
    private static final String PENDING_PREFIX = "warframe-community-preview-pending:";
    private static final int CHUNK_TARGET_BYTES = 256 * 1024;
    private static final String COMPRESSED_PREFIX = "gzip1:";

    // 分片大小必须匹配 RDT 的实际 JSON 编码，不能用 Jackson 2 的字符数估算。
    private final JsonCodec jsonCodec = new JsonCodec();

    @Resource
    private CacheService cacheService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeCommunityDataProperties properties;

    public void save(WarframeCommunityDatasetPreviewSnapshot snapshot) {
        List<WarframeCommunityDatasetPreviewSnapshotRecord> records = snapshot.records().stream()
                .map(WarframeCommunityPreviewStore::compactRecord).toList();
        List<List<WarframeCommunityDatasetPreviewSnapshotRecord>> recordChunks =
                splitChunks(records, snapshot, WarframeCommunityDatasetPreviewSnapshotRecord::recordKey);
        List<List<WarframeCommunityFieldDiff>> diffChunks =
                splitChunks(snapshot.diffs(), snapshot, WarframeCommunityFieldDiff::recordKey);
        WarframeCommunityDatasetPreviewSnapshot header =
                new WarframeCommunityDatasetPreviewSnapshot(
                        snapshot.previewId(), snapshot.dataset(), snapshot.source(),
                        snapshot.createdAt(), snapshot.expiresAt(), snapshot.recordCount(),
                        snapshot.addedCount(), snapshot.changedCount(), snapshot.conflictCount(),
                        List.of(), List.of(), snapshot.status(), snapshot.sourceCodes(),
                        recordChunks.size(), diffChunks.size(), Map.of(),
                        recordChunks.stream().map(List::size).toList(), snapshot.sources());
        try {
            writeChunks(RECORD_PREFIX, snapshot.previewId(), recordChunks);
            writeChunks(DIFF_PREFIX, snapshot.previewId(), diffChunks);
            Map<String, Integer> recordPositions = new HashMap<>();
            for (int position = 0; position < records.size(); position++) {
                recordPositions.put(records.get(position).operationId(), position);
            }
            cacheService.put(GROUP_PREFIX + snapshot.previewId(),
                    encodePreviewChunk(new WarframeCommunityDatasetPreviewIndex(
                            snapshot.groupsByDataset(), recordPositions), "社区数据预览分组索引"),
                    properties.getPreviewRetention());
            // header 是发布标记，不能在记录、差异和分组全部写完之前让读者看到。
            cacheService.put(PREVIEW_PREFIX + snapshot.previewId(), header,
                    properties.getPreviewRetention());
            // 全部来源失败或无差异的预览仍可查看，但不阻塞下一次刷新。
            if (!records.isEmpty()) {
                cacheService.put(PENDING_PREFIX + snapshot.dataset(), snapshot.previewId(),
                        properties.getPreviewRetention());
            }
        } catch (RuntimeException exception) {
            evictStorage(snapshot.previewId(), recordChunks.size(), diffChunks.size());
            if (!records.isEmpty()) {
                evictKey(PENDING_PREFIX + snapshot.dataset());
            }
            log.error("社区数据预览写入 Redis 失败, dataset={}, source={}, previewId={}, "
                            + "operations={}, recordChunks={}, diffChunks={}",
                    snapshot.dataset(), snapshot.source(), snapshot.previewId(), records.size(),
                    recordChunks.size(), diffChunks.size(), exception);
            throw new BusinessException(
                    WarframeManagementErrorCode.COMMUNITY_PREVIEW_CACHE_UNAVAILABLE,
                    "社区数据已完成拉取和解析，但预览快照写入 Redis 失败，请检查 Redis 连接和负载",
                    exception);
        }
    }

    public WarframeCommunityDatasetPreviewSnapshot header(String previewId) {
        BusinessAssert.isTrue(StrUtil.isNotBlank(previewId),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        // Jackson 2 的 JavaType 不能跨入 RDT 使用的 Jackson 3 TypeFactory。
        CacheEntry<?> entry = cacheService.get(PREVIEW_PREFIX + previewId.trim(), Object.class);
        BusinessAssert.isTrue(entry instanceof CacheEntry.Hit<?>,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        WarframeCommunityDatasetPreviewSnapshot header = cachedValue(
                ((CacheEntry.Hit<?>) entry).value(), WarframeCommunityDatasetPreviewSnapshot.class,
                "社区数据预览头");
        BusinessAssert.isTrue(header.expiresAt().isAfter(Instant.now()),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                "社区数据预览已过期，请重新生成");
        BusinessAssert.isTrue(header.status() == WarframeCommunityPreviewStatus.PENDING,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_ALREADY_MERGED);
        return header;
    }

    public WarframeCommunityDatasetPreviewSnapshot assemble(
            WarframeCommunityDatasetPreviewSnapshot header) {
        if (header.chunkCount() <= 0 && header.diffChunkCount() <= 0) {
            return header;
        }
        List<WarframeCommunityDatasetPreviewSnapshotRecord> records = new ArrayList<>();
        for (int index = 0; index < header.chunkCount(); index++) {
            records.addAll(recordChunk(header.previewId(), index));
        }
        List<WarframeCommunityFieldDiff> diffs = new ArrayList<>();
        for (int index = 0; index < header.diffChunkCount(); index++) {
            diffs.addAll(readChunk(DIFF_PREFIX, header.previewId(), index,
                    WarframeCommunityFieldDiff.class, "社区数据预览差异分片"));
        }
        return new WarframeCommunityDatasetPreviewSnapshot(
                header.previewId(), header.dataset(), header.source(), header.createdAt(),
                header.expiresAt(), header.recordCount(), header.addedCount(),
                header.changedCount(), header.conflictCount(),
                header.diffChunkCount() > 0 ? diffs : header.diffs(),
                header.chunkCount() > 0 ? records : header.records(),
                header.status(), header.sourceCodes(), header.chunkCount(),
                header.diffChunkCount(), header.groupsByDataset(), header.recordChunkSizes(),
                header.sources());
    }

    public List<WarframeCommunityDatasetPreviewSnapshotRecord> recordsInRange(
            WarframeCommunityDatasetPreviewSnapshot header, int from, int to) {
        if (from >= to) {
            return List.of();
        }
        List<WarframeCommunityDatasetPreviewSnapshotRecord> records = new ArrayList<>(to - from);
        int offset = 0;
        for (int index = 0; index < header.chunkCount() && offset < to; index++) {
            int chunkSize = header.recordChunkSizes().get(index);
            int chunkEnd = offset + chunkSize;
            if (chunkEnd <= from) {
                offset = chunkEnd;
                continue;
            }
            List<WarframeCommunityDatasetPreviewSnapshotRecord> chunk =
                    recordChunk(header.previewId(), index);
            BusinessAssert.isTrue(chunk.size() == chunkSize,
                    WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                    "社区数据预览记录分片数量无效，请重新生成");
            records.addAll(chunk.subList(Math.max(0, from - offset),
                    Math.min(chunk.size(), to - offset)));
            offset = chunkEnd;
        }
        return records;
    }

    public Map<String, List<WarframeCommunityDatasetPreviewGroup>> groups(
            WarframeCommunityDatasetPreviewSnapshot header) {
        return index(header).groupsByDataset();
    }

    public WarframeCommunityDatasetPreviewIndex index(
            WarframeCommunityDatasetPreviewSnapshot header) {
        if (header.groupsByDataset() != null && !header.groupsByDataset().isEmpty()) {
            return new WarframeCommunityDatasetPreviewIndex(header.groupsByDataset(), Map.of());
        }
        CacheEntry<?> entry = cacheService.get(GROUP_PREFIX + header.previewId(), Object.class);
        if (!(entry instanceof CacheEntry.Hit<?> hit)) {
            return new WarframeCommunityDatasetPreviewIndex(Map.of(), Map.of());
        }
        Object value = decodePreviewChunk(hit.value(), "社区数据预览分组索引");
        if (value instanceof WarframeCommunityDatasetPreviewIndex index) {
            return index;
        }
        BusinessAssert.isTrue(value instanceof Map<?, ?>,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                "社区数据预览分组索引格式无效，请重新生成");
        try {
            if (((Map<?, ?>) value).containsKey("groupsByDataset")) {
                return objectMapper.convertValue(value, WarframeCommunityDatasetPreviewIndex.class);
            }
            // 旧分组索引没有记录位置，仍允许通过完整快照审核。
            return new WarframeCommunityDatasetPreviewIndex(objectMapper.convertValue(value,
                    new TypeReference<Map<String,
                            List<WarframeCommunityDatasetPreviewGroup>>>() {}), Map.of());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                    "社区数据预览分组索引无法反序列化，请重新生成", exception);
        }
    }

    public List<WarframeCommunityDatasetPreviewSnapshotRecord> recordsAtPositions(
            WarframeCommunityDatasetPreviewSnapshot header, List<Integer> positions) {
        if (positions.isEmpty()) {
            return List.of();
        }
        int total = header.recordChunkSizes().stream().mapToInt(Integer::intValue).sum();
        BusinessAssert.isTrue(positions.stream().allMatch(
                        position -> position != null && position >= 0 && position < total),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                "社区数据预览记录位置无效，请重新生成");
        List<Integer> sorted = positions.stream().distinct().sorted().toList();
        Map<Integer, WarframeCommunityDatasetPreviewSnapshotRecord> records = new HashMap<>();
        int offset = 0;
        int next = 0;
        // 分组记录可能分散在多个分片中；每个相交分片最多读取一次。
        for (int index = 0; index < header.chunkCount() && next < sorted.size(); index++) {
            int chunkSize = header.recordChunkSizes().get(index);
            int end = offset + chunkSize;
            if (sorted.get(next) < end) {
                List<WarframeCommunityDatasetPreviewSnapshotRecord> chunk =
                        recordChunk(header.previewId(), index);
                BusinessAssert.isTrue(chunk.size() == chunkSize,
                        WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                        "社区数据预览记录分片数量无效，请重新生成");
                while (next < sorted.size() && sorted.get(next) < end) {
                    int position = sorted.get(next++);
                    records.put(position, chunk.get(position - offset));
                }
            }
            offset = end;
        }
        return positions.stream().map(records::get).toList();
    }

    public String pendingPreviewId(String dataset) {
        return cachedString(PENDING_PREFIX + dataset);
    }

    public void clearPending(String dataset) {
        cacheService.evict(PENDING_PREFIX + dataset);
    }

    public boolean isConsumed(String previewId) {
        return StrUtil.isNotBlank(cachedString(CONSUMED_PREFIX + previewId));
    }

    /** 只能在业务事务提交后调用；数据库回执仍是最终幂等依据。 */
    public void consume(String previewId, String dataset) {
        String id = previewId.trim();
        try {
            cacheService.put(CONSUMED_PREFIX + id, dataset, properties.getPreviewRetention());
        } catch (RuntimeException exception) {
            log.debug("社区预览消费标记写入失败, previewId={}", id, exception);
        }
        try {
            markMerged(id);
        } catch (RuntimeException exception) {
            log.debug("社区预览已合并状态写入失败, previewId={}", id, exception);
        }
        // 独立清理各键，某一个 Redis 操作失败不能阻止其他补偿步骤。
        evictKey(PENDING_PREFIX + dataset);
        evict(id);
    }

    public void evict(String previewId) {
        try {
            CacheEntry<?> entry = cacheService.get(PREVIEW_PREFIX + previewId, Object.class);
            if (entry instanceof CacheEntry.Hit<?> hit) {
                WarframeCommunityDatasetPreviewSnapshot snapshot = cachedValue(hit.value(),
                        WarframeCommunityDatasetPreviewSnapshot.class, "社区数据预览头");
                evictStorage(previewId, snapshot.chunkCount(), snapshot.diffChunkCount());
                return;
            }
        } catch (RuntimeException exception) {
            log.debug("社区预览清理时无法读取分片数量, previewId={}", previewId, exception);
        }
        evictStorage(previewId, 0, 0);
    }

    private void markMerged(String previewId) {
        CacheEntry<?> entry = cacheService.get(PREVIEW_PREFIX + previewId, Object.class);
        if (!(entry instanceof CacheEntry.Hit<?> hit)) {
            return;
        }
        WarframeCommunityDatasetPreviewSnapshot snapshot = cachedValue(hit.value(),
                WarframeCommunityDatasetPreviewSnapshot.class, "社区数据预览头");
        if (snapshot.status() != WarframeCommunityPreviewStatus.PENDING) {
            return;
        }
        cacheService.put(PREVIEW_PREFIX + previewId,
                new WarframeCommunityDatasetPreviewSnapshot(
                        snapshot.previewId(), snapshot.dataset(), snapshot.source(),
                        snapshot.createdAt(), snapshot.expiresAt(), snapshot.recordCount(),
                        snapshot.addedCount(), snapshot.changedCount(), snapshot.conflictCount(),
                        List.of(), List.of(), WarframeCommunityPreviewStatus.MERGED,
                        snapshot.sourceCodes(), snapshot.chunkCount(), snapshot.diffChunkCount(),
                        Map.of(), snapshot.recordChunkSizes(), snapshot.sources()),
                properties.getPreviewRetention());
    }

    private String cachedString(String key) {
        CacheEntry<String> entry = cacheService.get(key, String.class);
        return entry instanceof CacheEntry.Hit<?> hit && hit.value() instanceof String value
                ? value : null;
    }

    private <T> void writeChunks(String prefix, String previewId, List<List<T>> chunks) {
        for (int index = 0; index < chunks.size(); index++) {
            cacheService.put(chunkKey(prefix, previewId, index),
                    encodePreviewChunk(chunks.get(index), "社区数据预览分片"),
                    properties.getPreviewRetention());
        }
    }

    private <T> List<T> readChunk(
            String prefix, String previewId, int index, Class<T> type, String description) {
        CacheEntry<?> entry = cacheService.get(chunkKey(prefix, previewId, index), Object.class);
        BusinessAssert.isTrue(entry instanceof CacheEntry.Hit<?>,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                description + "已丢失，请重新生成");
        Object value = decodePreviewChunk(((CacheEntry.Hit<?>) entry).value(), description);
        BusinessAssert.isTrue(value instanceof List<?>,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                description + "格式无效，请重新生成");
        return ((List<?>) value).stream().map(item -> cachedValue(item, type, description)).toList();
    }

    private List<WarframeCommunityDatasetPreviewSnapshotRecord> recordChunk(
            String previewId, int index) {
        return readChunk(RECORD_PREFIX, previewId, index,
                WarframeCommunityDatasetPreviewSnapshotRecord.class, "社区数据预览记录分片")
                .stream().map(WarframeCommunityPreviewStore::restoreRecord).toList();
    }

    private <T> T cachedValue(Object value, Class<T> type, String description) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        BusinessAssert.isTrue(value instanceof Map<?, ?>,
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                description + "格式无效，请重新生成");
        try {
            return objectMapper.convertValue(value, type);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                    description + "无法反序列化，请重新生成", exception);
        }
    }

    private <T> List<List<T>> splitChunks(
            List<T> values,
            WarframeCommunityDatasetPreviewSnapshot snapshot,
            Function<T, String> recordKey) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<List<T>> chunks = new ArrayList<>();
        List<T> current = new ArrayList<>();
        int currentBytes = 2;
        for (T value : values) {
            int bytes = jsonBytes(value, "社区数据预览分片").length;
            BusinessAssert.isTrue(bytes <= CHUNK_TARGET_BYTES - 2,
                    WarframeManagementErrorCode.INVALID_IMPORT_FILE,
                    "社区数据预览单条记录过大：dataset=" + snapshot.dataset()
                            + ", source=" + snapshot.source() + ", recordKey=" + recordKey.apply(value)
                            + ", bytes=" + bytes + ", maxBytes=" + (CHUNK_TARGET_BYTES - 2));
            if (!current.isEmpty()
                    && currentBytes + bytes + 1 > CHUNK_TARGET_BYTES) {
                chunks.add(List.copyOf(current));
                current = new ArrayList<>();
                currentBytes = 2;
            }
            currentBytes += bytes + (current.isEmpty() ? 0 : 1);
            current.add(value);
        }
        if (!current.isEmpty()) {
            chunks.add(List.copyOf(current));
        }
        return chunks;
    }

    /** 带版本前缀的 Base64 仍由 RDT JSON 编码，只在含引号的存储结果更小时压缩。 */
    private Object encodePreviewChunk(Object value, String description) {
        byte[] json = jsonBytes(value, description);
        ByteArrayOutputStream output = new ByteArrayOutputStream(json.length);
        try (GZIPOutputStream gzip = new GZIPOutputStream(output)) {
            gzip.write(json);
        } catch (IOException exception) {
            throw new IllegalStateException("社区预览分片压缩失败：" + description, exception);
        }
        String encoded = COMPRESSED_PREFIX + Base64.getEncoder().encodeToString(output.toByteArray());
        return encoded.getBytes(StandardCharsets.UTF_8).length + 2 < json.length ? encoded : value;
    }

    private Object decodePreviewChunk(Object value, String description) {
        if (!(value instanceof String encoded)) {
            return value;
        }
        try {
            if (!encoded.startsWith(COMPRESSED_PREFIX)) {
                return jsonCodec.read(encoded, Object.class);
            }
            byte[] compressed = Base64.getDecoder().decode(encoded.substring(COMPRESSED_PREFIX.length()));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                gzip.transferTo(output);
            }
            return jsonCodec.read(output.toString(StandardCharsets.UTF_8), Object.class);
        } catch (IOException | RuntimeException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND,
                    description + "格式无效，请重新生成", exception);
        }
    }

    private byte[] jsonBytes(Object value, String description) {
        try {
            return jsonCodec.write(value).getBytes(StandardCharsets.UTF_8);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("社区预览 JSON 序列化失败：" + description, exception);
        }
    }

    private static WarframeCommunityDatasetPreviewSnapshotRecord compactRecord(
            WarframeCommunityDatasetPreviewSnapshotRecord record) {
        // originalJson 是并发合并校验基线，必须完整保留。
        return new WarframeCommunityDatasetPreviewSnapshotRecord(
                record.id(), record.operationId(), record.targetDataset(), record.recordKey(),
                record.identityKey(), record.locale(), record.upstreamJson(), record.upstreamPresent(),
                Objects.equals(record.currentJson(), record.upstreamJson()) ? null : record.currentJson(),
                record.originalJson(), "{}".equals(record.manualFieldsJson()) ? null : record.manualFieldsJson(),
                record.manualModified(), record.enabled(), record.recordSource(), record.sourceCode(),
                record.lastImportedAt(), record.changeType(), record.createdAt(), record.updatedAt());
    }

    private static WarframeCommunityDatasetPreviewSnapshotRecord restoreRecord(
            WarframeCommunityDatasetPreviewSnapshotRecord record) {
        return new WarframeCommunityDatasetPreviewSnapshotRecord(
                record.id(), record.operationId(), record.targetDataset(), record.recordKey(),
                record.identityKey(), record.locale(), record.upstreamJson(), record.upstreamPresent(),
                record.currentJson() == null ? record.upstreamJson() : record.currentJson(),
                record.originalJson(), record.manualFieldsJson() == null ? "{}" : record.manualFieldsJson(),
                record.manualModified(), record.enabled(), record.recordSource(), record.sourceCode(),
                record.lastImportedAt(), record.changeType(), record.createdAt(), record.updatedAt());
    }

    private void evictStorage(String previewId, int recordChunks, int diffChunks) {
        evictChunks(RECORD_PREFIX, previewId, recordChunks);
        evictChunks(DIFF_PREFIX, previewId, diffChunks);
        evictKey(GROUP_PREFIX + previewId);
        evictKey(PREVIEW_PREFIX + previewId);
    }

    private void evictChunks(String prefix, String previewId, int count) {
        for (int index = 0; index < count; index++) {
            evictKey(chunkKey(prefix, previewId, index));
        }
    }

    private void evictKey(String key) {
        try {
            cacheService.evict(key);
        } catch (RuntimeException exception) {
            log.debug("社区数据预览清理失败, key={}", key, exception);
        }
    }

    private static String chunkKey(String prefix, String previewId, int index) {
        return prefix + previewId + ":" + index;
    }
}
