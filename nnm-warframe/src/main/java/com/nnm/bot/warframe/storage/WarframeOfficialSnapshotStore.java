package com.nnm.bot.warframe.storage;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotValidator;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.fasterxml.jackson.core.JsonParser;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import com.nnm.bot.common.storage.ObjectStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialFieldChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordSelectionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialChangeCountsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialFileCategoryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import java.util.TreeMap;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/** 待审候选原文与分类差异存入对象存储；已采纳记录以数据库为准，不把整包数据放入 Redis。 */
@Component
public class WarframeOfficialSnapshotStore {
    @Resource
    private ObjectStorage storage;
    @Resource
    private WarframeOfficialSnapshotValidator validator;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialSnapshotParser parser;

    /**
     * 从对象存储逐条读取并校验整个版本文件，缺失或损坏的基线不可用于审批。
     *
     * @param sha256 快照文件的预期摘要。
     * @param domain 文件所属领域。
     * @param consumer 逐条接收类型化原始记录的回调。
     * @throws IOException 文件读取或摘要校验失败时抛出。
     */
    public void readRecords(String sha256, WarframeOfficialDataDomain domain,
        Consumer<OfficialImportRecordDTO> consumer) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
        try (InputStream input =
            new DigestInputStream(storage.open(DATASET_OBJECT_PREFIX + sha256 + "/" + domain.productPath()), digest)) {
            // 解析器会关闭传入流；校验边界仍需保留压缩流以复核完整原件摘要。
            parser.forEachRecord(new FilterInputStream(input) {
                @Override
                public void close() {}
            }, domain, consumer);
            input.transferTo(OutputStream.nullOutputStream());
        }
        if (!sha256.equalsIgnoreCase(HexFormat.of().formatHex(digest.digest()))) {
            throw new IOException("Official snapshot digest mismatch: " + domain.productPath());
        }
    }

    /**
     * 按稳定业务键从指定版本文件提取一条完整记录，不缓存整份原文。
     *
     * @param sha256 文件摘要。
     * @param domain 文件所属领域。
     * @param recordKey 记录稳定业务键。
     * @return 所属版本的匹配记录，文件中不存在时返回 null。
     * @throws IOException 快照缺失或损坏时抛出。
     */
    public OfficialImportRecordDTO record(String sha256, WarframeOfficialDataDomain domain, String recordKey)
        throws IOException {
        AtomicReference<OfficialImportRecordDTO> result = new AtomicReference<>();
        readRecords(sha256, domain, item -> {
            if (recordKey.equals(item.recordKey()))
                result.set(item);
        });
        return result.get();
    }

    private static final String PREVIEW_OBJECT_PREFIX = "warframe/official-preview/";
    private static final String DATASET_OBJECT_PREFIX = "warframe/official-dataset/";
    private static final int MAX_PREVIEW_PAGE_SIZE = 100;
    private static final List<ChangeType> CATEGORIES =
        List.of(ChangeType.ADDED, ChangeType.CHANGED, ChangeType.REMOVED);
    private static final int INDEX_STRIDE = 100;
    private static final int SCAN_BLOCK = 5_000;

    /** 每份 JSON 的差异保存为独立 JSONL；每百条一个字节偏移，翻页无需遍历整个对象。 */
    private static String groupKey(String key, String path, ChangeType category) {
        return key + "/" + path + "/" + category.name() + ".jsonl";
    }

    private static String indexKey(String key) {
        return key + "-index.json";
    }

    private record StoredChangeDTO(String recordKey, String displayName,
        List<WarframeOfficialFieldChangeDTO> fieldChanges) {
    }

    private record GroupIndex(int count, long bytes, List<Long> offsets) {
    }

    private record ChangeIndex(Map<String, Map<String, GroupIndex>> files) {
    }

    private final class GroupWriter implements AutoCloseable {
        private final Path file;
        private final List<Long> offsets = new ArrayList<>();
        private OutputStream output;
        private int count;
        private long bytes;

        private GroupWriter(Path directory) throws IOException {
            file = Files.createTempFile(directory, ".official-changes-", ".part");
        }

        private void append(WarframeOfficialRecordChangeDTO change) throws IOException {
            if (output == null)
                output = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.APPEND));
            if (count % INDEX_STRIDE == 0)
                offsets.add(bytes);
            byte[] record = objectMapper.writeValueAsBytes(
                new StoredChangeDTO(change.recordKey(), change.displayName(), change.fieldChanges()));
            output.write(record);
            output.write('\n');
            bytes = Math.addExact(bytes, record.length + 1L);
            count = Math.addExact(count, 1);
        }

        private GroupIndex index() {
            return new GroupIndex(count, bytes, List.copyOf(offsets));
        }

        @Override
        public void close() throws IOException {
            if (output != null) {
                output.close();
                output = null;
            }
        }
    }

    /** 冻结每份 JSON 的差异并写入索引；只有索引成功上传后批次才能发布。 */
    public OfficialChangeCountsDTO putChanges(String batchId, WarframeOfficialChangeSource changes, Path directory)
        throws IOException {
        return writeChanges(changesKey(batchId), changes, directory);
    }

    private OfficialChangeCountsDTO writeChanges(String key, WarframeOfficialChangeSource changes, Path directory)
        throws IOException {
        Map<String, Map<String, GroupWriter>> writers = new TreeMap<>();
        List<String> uploaded = new ArrayList<>();
        AtomicReference<String> activePath = new AtomicReference<>();
        try {
            try {
                changes.read(change -> {
                    try {
                        String path = change.path();
                        if (WarframeOfficialDataDomain.fromPath(path) == null)
                            throw new IOException("Invalid preview file: " + path);
                        ChangeType category = ChangeType.valueOf(change.changeType());
                        if (!CATEGORIES.contains(category))
                            throw new IOException("Invalid preview category: " + category);
                        if (activePath.get() != null && !activePath.get().equals(path))
                            closeGroup(writers.get(activePath.get()));
                        activePath.set(path);
                        Map<String, GroupWriter> groups = writers.computeIfAbsent(path, ignored -> new TreeMap<>());
                        GroupWriter writer = groups.get(category.name());
                        if (writer == null) {
                            writer = new GroupWriter(directory);
                            groups.put(category.name(), writer);
                        }
                        writer.append(change);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                });
            } catch (UncheckedIOException exception) {
                throw exception.getCause();
            }
            for (Map<String, GroupWriter> groups : writers.values())
                closeGroup(groups);
            Map<String, Map<String, GroupIndex>> indexed = new TreeMap<>();
            Map<String, Map<String, Integer>> counts = new TreeMap<>();
            for (var entry : writers.entrySet()) {
                Map<String, GroupIndex> fileIndex = new TreeMap<>();
                Map<String, Integer> fileCounts = new TreeMap<>();
                for (var category : entry.getValue().entrySet()) {
                    GroupWriter writer = category.getValue();
                    ChangeType type = ChangeType.valueOf(category.getKey());
                    String objectKey = groupKey(key, entry.getKey(), type);
                    uploaded.add(objectKey);
                    upload(objectKey, writer.file);
                    fileIndex.put(category.getKey(), writer.index());
                    fileCounts.put(category.getKey(), writer.count);
                }
                indexed.put(entry.getKey(), fileIndex);
                counts.put(entry.getKey(), fileCounts);
            }
            byte[] indexBytes = objectMapper.writeValueAsBytes(new ChangeIndex(indexed));
            uploaded.add(indexKey(key));
            try (InputStream input = new ByteArrayInputStream(indexBytes)) {
                storage.put(indexKey(key), input, indexBytes.length, "application/json");
            }
            return new OfficialChangeCountsDTO(counts);
        } catch (IOException | RuntimeException exception) {
            for (String objectKey : uploaded) {
                try {
                    storage.delete(objectKey);
                } catch (RuntimeException cleanup) {
                    exception.addSuppressed(cleanup);
                }
            }
            throw exception;
        } finally {
            for (Map<String, GroupWriter> groups : writers.values()) {
                try {
                    closeGroup(groups);
                } finally {
                    for (GroupWriter writer : groups.values())
                        Files.deleteIfExists(writer.file);
                }
            }
        }
    }

    private static void closeGroup(Map<String, GroupWriter> groups) throws IOException {
        if (groups != null)
            for (GroupWriter writer : groups.values())
                writer.close();
    }

    private void upload(String key, Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
        try (InputStream input = Files.newInputStream(file)) {
            storage.put(key, input, Files.size(file), "application/x-ndjson");
        }
    }

    private ChangeIndex readIndex(String key) throws IOException {
        try (InputStream input = storage.open(indexKey(key))) {
            ChangeIndex index = objectMapper.readValue(input, ChangeIndex.class);
            if (index == null || index.files() == null)
                throw new IOException("Invalid preview index");
            for (var file : index.files().entrySet()) {
                if (WarframeOfficialDataDomain.fromPath(file.getKey()) == null || file.getValue() == null)
                    throw new IOException("Invalid preview file index: " + file.getKey());
                for (var entry : file.getValue().entrySet()) {
                    GroupIndex group = entry.getValue();
                    if (!CATEGORIES.stream().anyMatch(category -> category.name().equals(entry.getKey()))
                        || group == null || group.count() <= 0 || group.bytes() <= 0 || group.offsets() == null
                        || group.offsets().size() != (group.count() - 1) / INDEX_STRIDE + 1
                        || !group.offsets().getFirst().equals(0L))
                        throw new IOException("Invalid preview group index: " + file.getKey() + "/" + entry.getKey());
                    long previous = -1;
                    for (long offset : group.offsets()) {
                        if (offset <= previous || offset >= group.bytes())
                            throw new IOException("Invalid preview offsets: " + file.getKey() + "/" + entry.getKey());
                        previous = offset;
                    }
                }
            }
            return index;
        }
    }

    /** 只读取当前页覆盖的字节范围；块结束必须正好处于记录边界。 */
    private void readBlock(String key, String path, ChangeType category, GroupIndex group, int first, int end,
        Consumer<WarframeOfficialRecordChangeDTO> consumer) throws IOException {
        long from = group.offsets().get(first / INDEX_STRIDE);
        long to = end == group.count() ? group.bytes() : group.offsets().get(end / INDEX_STRIDE);
        int read = 0;
        try (InputStream input = storage.openRange(groupKey(key, path, category), from, to - from);
            JsonParser reader = objectMapper.getFactory().createParser(input)) {
            while (reader.nextToken() != null) {
                StoredChangeDTO stored = objectMapper.readValue(reader, StoredChangeDTO.class);
                consumer.accept(new WarframeOfficialRecordChangeDTO(path, stored.recordKey(), stored.displayName(),
                    category.name(), stored.fieldChanges()));
                read++;
            }
        }
        if (read != end - first)
            throw new IOException("Truncated preview group: " + path + "/" + category);
    }

    private void forEachChange(String key, ChangeIndex index, String path, ChangeType category,
        Consumer<WarframeOfficialRecordChangeDTO> consumer) throws IOException {
        GroupIndex group = index.files().getOrDefault(path, Map.of()).get(category.name());
        if (group == null)
            return;
        for (int first = 0; first < group.count(); first += SCAN_BLOCK)
            readBlock(key, path, category, group, first, Math.min(first + SCAN_BLOCK, group.count()), consumer);
    }

    /**
     * 从不可变预览提取已批准的文件分类业务键；对象存储读取发生在分类事务之外。
     *
     * @param objectKey 冻结的原始差异对象键。
     * @param path 文件路径。
     * @param selection 冻结的审批选择。
     * @return 各分类获准采纳的业务键。
     * @throws IOException 差异对象不可读或不完整时抛出。
     */
    public Map<ChangeType, Set<String>> approvedKeys(String objectKey, String path, WarframeOfficialApplyDTO selection)
        throws IOException {
        ChangeIndex index = readIndex(objectKey);
        Map<ChangeType, Set<String>> result = new HashMap<>();
        for (ChangeType category : CATEGORIES) {
            WarframeOfficialFileCategoryDTO group = new WarframeOfficialFileCategoryDTO(path, category.name());
            Set<String> keys = new HashSet<>();
            selection.changes().stream()
                .filter(item -> path.equals(item.path()) && category.name().equals(item.changeType()))
                .map(WarframeOfficialRecordSelectionDTO::recordKey).forEach(keys::add);
            if (selection.allGroups().contains(group)) {
                forEachChange(objectKey, index, path, category, change -> keys.add(change.recordKey()));
                selection.excluded().stream()
                    .filter(item -> path.equals(item.path()) && category.name().equals(item.changeType()))
                    .map(WarframeOfficialRecordSelectionDTO::recordKey).forEach(keys::remove);
            }
            result.put(category, Set.copyOf(keys));
        }
        return Map.copyOf(result);
    }

    /**
     * 根据文件和分类的稀疏索引只读取当前页的字节范围。
     *
     * @param objectKey 待审差异的对象键前缀。
     * @param path 所属官方 JSON 路径。
     * @param current 从一开始的页码。
     * @param size 每页最多返回的记录数。
     * @param changeType 差异分类。
     * @return 当前页及分类记录总数。
     * @throws IOException 索引或记录对象缺失、损坏时抛出。
     */
    public PageResult<WarframeOfficialRecordChangeDTO> changes(String objectKey, String path, int current, int size,
        String changeType) throws IOException {
        ChangeIndex index = readIndex(objectKey);
        int page = Math.max(1, current);
        int limit = Math.max(1, Math.min(MAX_PREVIEW_PAGE_SIZE, size));
        BusinessAssert.isTrue(WarframeOfficialDataDomain.fromPath(path) != null && index.files().containsKey(path),
            WarframeManagementErrorCode.DATASET_INVALID, "所选文件不属于待审核预览：" + path);
        ChangeType category =
            CATEGORIES.stream().filter(type -> type.name().equals(changeType)).findFirst().orElse(null);
        BusinessAssert.notNull(category, WarframeManagementErrorCode.DATASET_INVALID, "预览差异分类不合法");
        GroupIndex group = index.files().get(path).get(category.name());
        int total = group == null ? 0 : group.count();
        long skip = (long)(page - 1) * limit;
        List<WarframeOfficialRecordChangeDTO> records = new ArrayList<>();
        if (skip < total) {
            int first = (int)(skip / INDEX_STRIDE) * INDEX_STRIDE;
            int end = Math.min(total, (int)((skip + limit + INDEX_STRIDE - 1) / INDEX_STRIDE) * INDEX_STRIDE);
            int[] indexWithinGroup = {first};
            readBlock(objectKey, path, category, group, first, end, change -> {
                if (indexWithinGroup[0] >= skip && records.size() < limit)
                    records.add(change);
                indexWithinGroup[0]++;
            });
        }
        return PageResult.of(records, page, limit, total);
    }

    /**
     * 冻结过滤后的文件差异；全选整组不下载记录，未触及的组由对象存储端复制。
     *
     * @param sourceKey 待审核差异的原对象键前缀。
     * @param targetKey 新冻结差异的对象键前缀。
     * @param selection 已提交的文件分类与单项选择。
     * @param directory 暂存局部变更文件的目录。
     * @return 剩余差异的计数、位置和选中数。
     * @throws IOException 对象读取、复制或写入失败时抛出。
     */
    public OfficialFilteredChangesDTO filterChanges(String sourceKey, String targetKey,
        WarframeOfficialApplyDTO selection, Path directory) throws IOException {
        ChangeIndex source = readIndex(sourceKey);
        if (selection == null || selection.allGroups().stream()
            .anyMatch(group -> group == null || group.path() == null || group.changeType() == null
                || !source.files().containsKey(group.path())
                || !source.files().get(group.path()).containsKey(group.changeType())))
            throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "所选文件分类不属于待审核预览");
        Set<WarframeOfficialRecordSelectionDTO> selected = new HashSet<>(selection.changes());
        Set<WarframeOfficialRecordSelectionDTO> excluded = new HashSet<>(selection.excluded());
        if (selected.size() != selection.changes().size() || excluded.size() != selection.excluded().size()
            || !Collections.disjoint(selected, excluded))
            throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "预览选择包含重复或相互冲突的记录");
        Set<WarframeOfficialFileCategoryDTO> overriddenGroups = new HashSet<>();
        for (WarframeOfficialRecordSelectionDTO item : selection.changes()) {
            WarframeOfficialFileCategoryDTO group = new WarframeOfficialFileCategoryDTO(item.path(), item.changeType());
            if (item.path() == null || item.changeType() == null || item.recordKey() == null
                || !source.files().containsKey(item.path())
                || !source.files().get(item.path()).containsKey(item.changeType()))
                throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "所选记录不属于待审核预览");
            overriddenGroups.add(group);
        }
        for (WarframeOfficialRecordSelectionDTO item : selection.excluded()) {
            WarframeOfficialFileCategoryDTO group = new WarframeOfficialFileCategoryDTO(item.path(), item.changeType());
            if (item.recordKey() == null || !selection.allGroups().contains(group))
                throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "排除记录不属于已选文件分类");
            overriddenGroups.add(group);
        }
        Set<WarframeOfficialRecordSelectionDTO> matchedSelected = new HashSet<>();
        Set<WarframeOfficialRecordSelectionDTO> matchedExcluded = new HashSet<>();
        Set<String> selectedPaths = new HashSet<>();
        int[] matched = {0};
        Map<String, Map<String, GroupIndex>> remaining = new TreeMap<>();
        List<String> uploaded = new ArrayList<>();
        try {
            for (var file : source.files().entrySet()) {
                String path = file.getKey();
                Map<String, GroupIndex> kept = new TreeMap<>();
                for (ChangeType category : CATEGORIES) {
                    GroupIndex group = file.getValue().get(category.name());
                    if (group == null)
                        continue;
                    WarframeOfficialFileCategoryDTO groupSelection =
                        new WarframeOfficialFileCategoryDTO(path, category.name());
                    boolean all = selection.allGroups().contains(groupSelection);
                    String destination = groupKey(targetKey, path, category);
                    if (!overriddenGroups.contains(groupSelection)) {
                        if (all) {
                            matched[0] = Math.addExact(matched[0], group.count());
                            selectedPaths.add(path);
                        } else {
                            uploaded.add(destination);
                            storage.copy(groupKey(sourceKey, path, category), destination);
                            kept.put(category.name(), group);
                        }
                        continue;
                    }
                    GroupWriter[] writer = {null};
                    try {
                        try {
                            forEachChange(sourceKey, source, path, category, change -> {
                                WarframeOfficialRecordSelectionDTO key =
                                    new WarframeOfficialRecordSelectionDTO(path, change.recordKey(), category.name());
                                if (excluded.contains(key) && all)
                                    matchedExcluded.add(key);
                                boolean keep = !selected.contains(key) && (!all || excluded.contains(key));
                                if (keep) {
                                    try {
                                        if (writer[0] == null)
                                            writer[0] = new GroupWriter(directory);
                                        writer[0].append(change);
                                    } catch (IOException exception) {
                                        throw new UncheckedIOException(exception);
                                    }
                                } else {
                                    if (selected.contains(key))
                                        matchedSelected.add(key);
                                    matched[0] = Math.addExact(matched[0], 1);
                                    selectedPaths.add(path);
                                }
                            });
                        } catch (UncheckedIOException exception) {
                            throw exception.getCause();
                        }
                        if (writer[0] != null) {
                            writer[0].close();
                            uploaded.add(destination);
                            upload(destination, writer[0].file);
                            kept.put(category.name(), writer[0].index());
                        }
                    } finally {
                        if (writer[0] != null) {
                            writer[0].close();
                            Files.deleteIfExists(writer[0].file);
                        }
                    }
                }
                if (!kept.isEmpty())
                    remaining.put(path, kept);
            }
            if (!matchedSelected.equals(selected) || !matchedExcluded.equals(excluded))
                throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "所选记录已不属于待审核预览");
            byte[] indexBytes = objectMapper.writeValueAsBytes(new ChangeIndex(remaining));
            uploaded.add(indexKey(targetKey));
            try (InputStream input = new ByteArrayInputStream(indexBytes)) {
                storage.put(indexKey(targetKey), input, indexBytes.length, "application/json");
            }
            Map<String, Map<String, Integer>> counts = new TreeMap<>();
            remaining.forEach((path, groups) -> {
                Map<String, Integer> fileCounts = new TreeMap<>();
                groups.forEach((category, group) -> fileCounts.put(category, group.count()));
                counts.put(path, fileCounts);
            });
            OfficialChangeCountsDTO result = new OfficialChangeCountsDTO(counts);
            return new OfficialFilteredChangesDTO(targetKey, result.totals(), result.files(), result.files().keySet(),
                selectedPaths, matched[0]);
        } catch (IOException | RuntimeException exception) {
            for (String objectKey : uploaded) {
                try {
                    storage.delete(objectKey);
                } catch (RuntimeException cleanup) {
                    exception.addSuppressed(cleanup);
                }
            }
            throw exception;
        }
    }

    /**
     * 预览从未发布时按本轮独占前缀清理：即使中断发生在索引上传前也能回收分组对象。
     *
     * @param batchId 本轮上传账本标识。
     */
    public void deleteAbandonedChanges(String batchId) {
        storage.deleteExpired(PREVIEW_OBJECT_PREFIX + batchId + "/", Instant.now());
    }

    /** 审核事务切换对象键前，写入新的三类对象键前缀。 */
    public static String nextChangesKey(String batchId) {
        return PREVIEW_OBJECT_PREFIX + batchId + "/changes-" + UUID.randomUUID();
    }

    /** 当前批次的初始三类差异对象键前缀。 */
    public static String changesKey(String batchId) {
        return PREVIEW_OBJECT_PREFIX + batchId + "/changes";
    }

    /**
     * 写入指定键对应的数据。
     *
     * @param entry 条目。
     * @param file 本次上传或读取的文件。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public void put(FileEntry entry, Path file) throws IOException {
        try (InputStream input = Files.newInputStream(file)) {
            storage.put(key(entry), input, entry.bytes(), entry.mediaType());
        }
    }

    /**
     * 恢复已经保存的业务状态或预览内容。
     *
     * @param entry 条目。
     * @param target 本次操作的目标对象或路径。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public void restore(FileEntry entry, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), ".dataset-", ".part");
        try {
            try (InputStream input = storage.open(key(entry)); OutputStream output = Files.newOutputStream(temporary)) {
                byte[] buffer = new byte[32 * 1024];
                long size = 0;
                int count;
                while ((count = input.read(buffer)) != -1) {
                    if (count > entry.bytes() - size) {
                        throw new IOException("Stored dataset exceeds manifest bytes: " + entry.path());
                    }
                    output.write(buffer, 0, count);
                    size += count;
                }
            }
            // 对象存储也不是信任边界：审批前重新校验，不能让损坏对象进入事务。
            validator.file(entry, temporary);
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param entry 条目。
     */
    public void delete(FileEntry entry) {
        storage.delete(key(entry));
    }

    /**
     * 仅回收不再被任何工作项读取的文件分组，索引仍保持不可变，供未完成文件按路径读取。 删除是幂等的；失败时留给下一轮清理重试。
     *
     * @param objectKey 原始或过滤后的差异对象键。
     * @param requiredPaths 仍会读取差异分组的文件路径。
     */
    public void deleteUnusedChangeGroups(String objectKey, Set<String> requiredPaths) {
        if (storage.stat(indexKey(objectKey)).isEmpty())
            return;
        ChangeIndex index;
        try {
            index = readIndex(objectKey);
        } catch (IOException exception) {
            throw new SystemException(CommonErrorCode.RESOURCE_CLEANUP_FAILED, "官方预览索引损坏，无法完成逐文件清理: " + objectKey,
                exception);
        }
        for (var file : index.files().entrySet()) {
            if (requiredPaths.contains(file.getKey()))
                continue;
            for (String category : file.getValue().keySet())
                storage.delete(groupKey(objectKey, file.getKey(), ChangeType.valueOf(category)));
        }
    }

    /**
     * 删除已经结束或失效批次的预览变更。
     *
     * @param batchId 当前冻结导入批次的标识。
     */
    public void deleteChanges(String objectKey) {
        if (storage.stat(indexKey(objectKey)).isEmpty())
            return;
        ChangeIndex index;
        try {
            index = readIndex(objectKey);
        } catch (IOException exception) {
            throw new SystemException(CommonErrorCode.RESOURCE_CLEANUP_FAILED, "官方预览索引损坏，无法完成清理: " + objectKey,
                exception);
        }
        for (var entry : index.files().entrySet())
            for (String category : entry.getValue().keySet())
                storage.delete(groupKey(objectKey, entry.getKey(), ChangeType.valueOf(category)));
        storage.delete(indexKey(objectKey));
    }

    /**
     * 构造当前对象的稳定查找键。
     *
     * @param entry 条目。
     * @return 构造当前对象的稳定查找键对应的文本。
     */
    private static String key(FileEntry entry) {
        return DATASET_OBJECT_PREFIX + entry.sha256() + "/" + entry.path();
    }
}
