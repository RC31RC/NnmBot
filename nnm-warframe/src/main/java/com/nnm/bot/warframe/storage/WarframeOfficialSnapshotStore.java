package com.nnm.bot.warframe.storage;

import com.fasterxml.jackson.core.JsonEncoding;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotValidator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.HashMap;
import com.nnm.bot.common.storage.ObjectStorage;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordSelectionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.UUID;
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

/** 内容寻址的整包文件存储；数据库只保存发布清单，不把数百 MB 放入 Redis。 */
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
        try (InputStream input = new DigestInputStream(
            storage.open("warframe/official-dataset/" + sha256 + "/" + domain.productPath()), digest)) {
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

    /**
     * 保存当前预览已经冻结的变更。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @param changes 变更。
     * @param directory 本次读取或存储使用的目录。
     * @return 以本方法约定的业务键组织的数据映射。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public Map<String, Integer> putChanges(String batchId, WarframeOfficialChangeSource changes, Path directory)
        throws IOException {
        Path file = directory.resolve("record-changes.json");
        Path temporary = Files.createTempFile(directory, ".record-changes-", ".part");
        Map<String, Integer> counts = new HashMap<>();
        try {
            try (var writer = objectMapper.getFactory().createGenerator(temporary.toFile(), JsonEncoding.UTF8)) {
                writer.writeStartArray();
                changes.read(change -> {
                    try {
                        objectMapper.writeValue(writer, change);
                        counts.merge(change.changeType(), 1, Math::addExact);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                });
                writer.writeEndArray();
            } catch (UncheckedIOException exception) {
                throw exception.getCause();
            }
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            try (InputStream input = Files.newInputStream(file)) {
                storage.put(changesKey(batchId), input, Files.size(file), "application/json");
            }
            return Map.copyOf(counts);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param objectKey 已保存字段差异文件的对象存储 key。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public PageResult<WarframeOfficialRecordChangeDTO> changes(String objectKey, int current, int size,
        String changeType) throws IOException {
        int page = Math.max(1, current);
        int limit = Math.max(1, Math.min(100, size));
        String filter = changeType == null || changeType.isBlank() ? null : changeType;
        long skip = (long)(page - 1) * limit;
        long total = 0;
        List<WarframeOfficialRecordChangeDTO> result = new ArrayList<>();
        try (InputStream input = storage.open(objectKey);
            JsonParser parser = objectMapper.getFactory().createParser(input)) {
            if (parser.nextToken() != JsonToken.START_ARRAY)
                throw new IOException("Invalid stored preview changes");
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.currentToken() == null)
                    throw new IOException("Truncated stored preview changes");
                if (filter == null && total >= skip && result.size() < limit) {
                    result.add(objectMapper.readValue(parser, WarframeOfficialRecordChangeDTO.class));
                    total++;
                } else if (filter == null) {
                    parser.skipChildren();
                    total++;
                } else {
                    WarframeOfficialRecordChangeDTO change =
                        objectMapper.readValue(parser, WarframeOfficialRecordChangeDTO.class);
                    if (filter.equals(change.changeType())) {
                        if (total >= skip && result.size() < limit)
                            result.add(change);
                        total++;
                    }
                }
            }
            if (parser.nextToken() != null)
                throw new IOException("Trailing stored preview data");
        }
        return PageResult.of(result, page, limit, total);
    }

    /**
     * 从冻结差异中移除已选记录，并以新对象键保存剩余审核状态。
     *
     * @param sourceKey 当前冻结差异对象键。
     * @param targetKey 新冻结差异对象键。
     * @param selected 已勾选记录的稳定业务键集合。
     * @param directory 本次读取或存储使用的目录。
     * @return 剩余差异计数、路径及已匹配的选择。
     * @throws IOException 对象读取、解析或写入失败时抛出。
     */
    public OfficialFilteredChangesDTO filterChanges(String sourceKey, String targetKey,
        Set<WarframeOfficialRecordSelectionDTO> selected, Path directory) throws IOException {
        Path temporary = Files.createTempFile(directory, ".record-changes-", ".part");
        Map<String, Integer> counts = new HashMap<>();
        Set<String> remainingPaths = new HashSet<>();
        Set<WarframeOfficialRecordSelectionDTO> matched = new HashSet<>();
        try {
            try (InputStream input = storage.open(sourceKey);
                JsonParser parser = objectMapper.getFactory().createParser(input);
                var writer = objectMapper.getFactory().createGenerator(temporary.toFile(), JsonEncoding.UTF8)) {
                if (parser.nextToken() != JsonToken.START_ARRAY)
                    throw new IOException("Invalid stored preview changes");
                writer.writeStartArray();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    if (parser.currentToken() == null)
                        throw new IOException("Truncated stored preview changes");
                    WarframeOfficialRecordChangeDTO change =
                        objectMapper.readValue(parser, WarframeOfficialRecordChangeDTO.class);
                    WarframeOfficialRecordSelectionDTO selection =
                        new WarframeOfficialRecordSelectionDTO(change.path(), change.recordKey());
                    if (selected.contains(selection)) {
                        matched.add(selection);
                        continue;
                    }
                    objectMapper.writeValue(writer, change);
                    counts.merge(change.changeType(), 1, Math::addExact);
                    remainingPaths.add(change.path());
                }
                if (parser.nextToken() != null)
                    throw new IOException("Trailing stored preview data");
                writer.writeEndArray();
            }
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            try (InputStream input = Files.newInputStream(temporary)) {
                storage.put(targetKey, input, Files.size(temporary), "application/json");
            }
            Set<String> identities = new HashSet<>();
            for (WarframeOfficialRecordSelectionDTO selection : matched) {
                WarframeOfficialDataDomain domain = WarframeOfficialDataDomain.fromPath(selection.path());
                if (domain == null)
                    throw new IOException("Invalid selected path in preview: " + selection.path());
                identities.add(domain.domainCode() + ":" + selection.recordKey());
            }
            return new OfficialFilteredChangesDTO(targetKey, counts, remainingPaths, identities, matched.size());
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * 创建当前批次的新差异对象键，避免原快照在数据库事务提交前被覆盖。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @return 新的内容对象键。
     */
    public static String nextChangesKey(String batchId) {
        return "warframe/official-preview/" + batchId + "/changes-" + UUID.randomUUID() + ".json";
    }

    /**
     * 在开启采纳事务之前读取冻结差异，仅传递发生变化的记录身份。
     *
     * @param objectKey 已保存审核差异的对象存储 key。
     * @return 需要重新写入主子表的稳定业务身份。
     * @throws IOException 冻结差异不存在或损坏时抛出。
     */
    public Set<String> changedIdentities(String objectKey) throws IOException {
        Set<String> identities = new HashSet<>();
        try (InputStream input = storage.open(objectKey);
            JsonParser changes = objectMapper.getFactory().createParser(input)) {
            if (changes.nextToken() != JsonToken.START_ARRAY)
                throw new IOException("Invalid stored preview changes");
            while (changes.nextToken() != JsonToken.END_ARRAY) {
                if (changes.currentToken() == null)
                    throw new IOException("Truncated stored preview changes");
                WarframeOfficialRecordChangeDTO change =
                    objectMapper.readValue(changes, WarframeOfficialRecordChangeDTO.class);
                WarframeOfficialDataDomain domain = WarframeOfficialDataDomain.fromPath(change.path());
                if (domain == null || change.recordKey() == null) {
                    throw new IOException("Invalid record identity in stored preview changes");
                }
                identities.add(domain.domainCode() + ":" + change.recordKey());
            }
            if (changes.nextToken() != null)
                throw new IOException("Trailing stored preview data");
        }
        return Set.copyOf(identities);
    }

    /**
     * 构造冻结预览变更使用的存储键。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @return 构造冻结预览变更使用的存储键对应的文本。
     */
    public static String changesKey(String batchId) {
        return "warframe/official-preview/" + batchId + "/changes.json";
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
     * 删除已经结束或失效批次的预览变更。
     *
     * @param batchId 当前冻结导入批次的标识。
     */
    public void deleteChanges(String objectKey) {
        storage.delete(objectKey);
    }

    /**
     * 构造当前对象的稳定查找键。
     *
     * @param entry 条目。
     * @return 构造当前对象的稳定查找键对应的文本。
     */
    private static String key(FileEntry entry) {
        return "warframe/official-dataset/" + entry.sha256() + "/" + entry.path();
    }
}
