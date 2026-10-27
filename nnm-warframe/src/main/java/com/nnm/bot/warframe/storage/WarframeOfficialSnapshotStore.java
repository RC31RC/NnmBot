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
import java.util.ArrayList;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
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
     * @param batchId 当前冻结导入批次的标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public PageResult<WarframeOfficialRecordChangeDTO> changes(String batchId, int current, int size) throws IOException {
        int page = Math.max(1, current);
        int limit = Math.max(1, Math.min(100, size));
        long skip = (long) (page - 1) * limit;
        long total = 0;
        List<WarframeOfficialRecordChangeDTO> result = new ArrayList<>();
        try (InputStream input = storage.open(changesKey(batchId));
             JsonParser parser = objectMapper.getFactory().createParser(input)) {
            if (parser.nextToken() != JsonToken.START_ARRAY) throw new IOException("Invalid stored preview changes");
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.currentToken() == null) throw new IOException("Truncated stored preview changes");
                if (total >= skip && result.size() < limit) {
                    result.add(objectMapper.readValue(parser, WarframeOfficialRecordChangeDTO.class));
                } else {
                    parser.skipChildren();
                }
                total++;
            }
            if (parser.nextToken() != null) throw new IOException("Trailing stored preview data");
        }
        return PageResult.of(result, page, limit, total);
    }

    /**
     * 构造冻结预览变更使用的存储键。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @return 构造冻结预览变更使用的存储键对应的文本。
     */
    private static String changesKey(String batchId) {
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
            try (InputStream input = storage.open(key(entry));
                 OutputStream output = Files.newOutputStream(temporary)) {
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
    public void deleteChanges(String batchId) {
        storage.delete(changesKey(batchId));
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
