package com.nnm.bot.core.onebot.message;

import java.util.Arrays;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.enums.ActionPath;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 将 NnmBot 文件转换为 OneBot 进程可读取的文件引用。
 */
@Component
public class OneBotFileTransferService {

    private static final ActionPath UPLOAD_FILE_STREAM = () -> "upload_file_stream";

    @Resource
    private OneBotProperties properties;

    public StagedFile stage(Bot bot, BotFile file) throws IOException {
        long fileSize = file.size();
        if (this.useStreamTransfer(bot)) {
            return this.uploadStream(bot, file, fileSize);
        }
        return this.stageLocalFile(file, fileSize);
    }

    private StagedFile stageLocalFile(BotFile file, long fileSize) throws IOException {
        if (file.pathBacked()) {
            return new StagedFile(
                    file.path().toString(), "local-path", fileSize, null);
        }
        Path directory = this.properties.getFileStagingDirectory()
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(directory);
        Path temporaryFile = Files.createTempFile(
                directory, UUID.randomUUID() + "-", safeName(file.fileName()));
        boolean completed = false;
        try {
            Files.write(temporaryFile, file.content());
            completed = true;
            return new StagedFile(
                    temporaryFile.toString(), "local-path", fileSize, temporaryFile);
        } finally {
            if (!completed) {
                Files.deleteIfExists(temporaryFile);
            }
        }
    }

    private StagedFile uploadStream(Bot bot, BotFile file, long fileSize) throws IOException {
        if (fileSize <= 0L) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "OneBot 不支持发送空文件: " + file.fileName());
        }
        if (fileSize > this.properties.getFileStreamMaxBytes()) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "OneBot 分片上传文件超过限制: file=" + file.fileName()
                            + ", size=" + fileSize
                            + ", max=" + this.properties.getFileStreamMaxBytes());
        }

        int chunkSize = this.properties.getFileStreamChunkSizeBytes();
        long totalChunksLong = (fileSize + chunkSize - 1L) / chunkSize;
        if (totalChunksLong > Integer.MAX_VALUE) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "OneBot 分片数量超过限制: " + totalChunksLong);
        }

        String streamId = UUID.randomUUID().toString();
        String remoteName = streamId + fileExtension(file.fileName());
        int totalChunks = (int) totalChunksLong;
        int chunkIndex = 0;
        try (InputStream input = file.openStream()) {
            byte[] buffer = new byte[chunkSize];
            int read;
            while ((read = input.readNBytes(buffer, 0, buffer.length)) > 0) {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("stream_id", streamId);
                params.put("chunk_data", Base64.getEncoder().encodeToString(
                        read == buffer.length ? buffer : Arrays.copyOf(buffer, read)));
                params.put("chunk_index", chunkIndex);
                params.put("total_chunks", totalChunks);
                params.put("file_size", fileSize);
                params.put("filename", remoteName);
                params.put("file_retention",
                        this.properties.fileStreamRetentionMillis());
                this.ensureStreamSuccess(
                        bot.customRequest(UPLOAD_FILE_STREAM, params),
                        file, fileSize, "chunk " + chunkIndex + "/" + totalChunks);
                chunkIndex++;
            }
        }

        Map<String, Object> completeParams = new LinkedHashMap<>();
        completeParams.put("stream_id", streamId);
        completeParams.put("is_complete", true);
        ActionData<?> completed = bot.customRequest(UPLOAD_FILE_STREAM, completeParams);
        this.ensureStreamSuccess(completed, file, fileSize, "complete");
        String remotePath = dataText(completed, "file_path");
        if (StrUtil.isBlank(remotePath)) {
            throw this.streamFailure(
                    file, fileSize, completed,
                    "OneBot 未返回分片合并后的 file_path");
        }
        return new StagedFile(remotePath, "websocket-stream", fileSize, null);
    }

    private void ensureStreamSuccess(ActionData<?> result, BotFile file,
                                     long fileSize, String phase) {
        if (ObjectUtil.isNull(result)
                || !"ok".equalsIgnoreCase(result.getStatus())
                || (ObjectUtil.isNotNull(result.getRetCode()) && result.getRetCode() != 0)) {
            throw this.streamFailure(
                    file, fileSize, result,
                    "phase=" + phase
                            + "; 远端 OneBot 需要支持 NapCat upload_file_stream");
        }
    }

    private BusinessException streamFailure(BotFile file, long fileSize,
                                            ActionData<?> result, String detail) {
        return new BusinessException(
                BotChannelErrorCode.FILE_STAGING_FAILED,
                "OneBot 远程文件分片上传失败: file=" + file.fileName()
                        + ", size=" + fileSize
                        + ", result=" + result
                        + ", detail=" + detail);
    }

    private boolean useStreamTransfer(Bot bot) {
        return switch (this.properties.getFileTransferMode()) {
            case STREAM -> true;
            case LOCAL_PATH -> false;
            case AUTO -> !isLoopback(bot);
        };
    }

    private static boolean isLoopback(Bot bot) {
        WebSocketSession session = bot.getSession();
        URI uri = ObjectUtil.isNull(session) ? null : session.getUri();
        String host = ObjectUtil.isNull(uri) ? null : uri.getHost();
        if (StrUtil.isBlank(host)) {
            return false;
        }
        String normalized = host.toLowerCase(Locale.ROOT);
        return "localhost".equals(normalized)
                || "::1".equals(normalized)
                || normalized.startsWith("127.");
    }

    private static String dataText(ActionData<?> result, String name) {
        if (ObjectUtil.isNull(result) || !(result.getData() instanceof Map<?, ?> data)) {
            return null;
        }
        Object value = data.get(name);
        return ObjectUtil.isNull(value) ? null : value.toString();
    }

    private static String safeName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String fileExtension(String fileName) {
        String safeName = safeName(fileName);
        int separator = safeName.lastIndexOf('.');
        if (separator < 0 || separator == safeName.length() - 1) {
            return "";
        }
        return safeName.substring(separator);
    }

    public record StagedFile(
            String uploadReference,
            String transferMode,
            long fileSize,
            Path cleanupPath
    ) implements AutoCloseable {

        @Override
        public void close() throws IOException {
            if (ObjectUtil.isNotNull(cleanupPath)) {
                Files.deleteIfExists(cleanupPath);
            }
        }
    }
}
