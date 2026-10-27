package com.nnm.bot.core.onebot.config;

import com.nnm.bot.core.constant.onebot.OneBotConstants.FileTransferMode;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;

/**
 * OneBot 协议级配置。
 *
 * <p>实例的连接信息和启停状态由数据库管理，Properties 只保留协议级参数。</p>
 */
@ConfigurationProperties(prefix = "nnm.bot.channels.onebot")
public class OneBotProperties {

    private Duration probeTimeout = Duration.ofSeconds(8);
    private Duration sendTimeLimit = Duration.ofSeconds(30);
    private int sendBufferSizeBytes = 16 * 1024 * 1024;
    private FileTransferMode fileTransferMode = FileTransferMode.AUTO;
    private Path fileStagingDirectory =
            Path.of(System.getProperty("user.dir"), "data", "onebot-files");
    private int fileStreamChunkSizeBytes = 512 * 1024;
    private long fileStreamMaxBytes = 128L * 1024 * 1024;
    private Duration fileStreamRetention = Duration.ofMinutes(15);

    public Duration getProbeTimeout() {
        return probeTimeout;
    }

    public void setProbeTimeout(Duration probeTimeout) {
        this.probeTimeout = probeTimeout;
    }

    public Duration getSendTimeLimit() {
        return sendTimeLimit;
    }

    public void setSendTimeLimit(Duration sendTimeLimit) {
        this.sendTimeLimit = sendTimeLimit;
    }

    public int getSendBufferSizeBytes() {
        return sendBufferSizeBytes;
    }

    public void setSendBufferSizeBytes(int sendBufferSizeBytes) {
        this.sendBufferSizeBytes = sendBufferSizeBytes;
    }

    public FileTransferMode getFileTransferMode() {
        return fileTransferMode;
    }

    public void setFileTransferMode(FileTransferMode fileTransferMode) {
        this.fileTransferMode = fileTransferMode;
    }

    public Path getFileStagingDirectory() {
        return fileStagingDirectory;
    }

    public void setFileStagingDirectory(Path fileStagingDirectory) {
        this.fileStagingDirectory = fileStagingDirectory;
    }

    public int getFileStreamChunkSizeBytes() {
        return fileStreamChunkSizeBytes;
    }

    public void setFileStreamChunkSizeBytes(int fileStreamChunkSizeBytes) {
        this.fileStreamChunkSizeBytes = fileStreamChunkSizeBytes;
    }

    public long getFileStreamMaxBytes() {
        return fileStreamMaxBytes;
    }

    public void setFileStreamMaxBytes(long fileStreamMaxBytes) {
        this.fileStreamMaxBytes = fileStreamMaxBytes;
    }

    public Duration getFileStreamRetention() {
        return fileStreamRetention;
    }

    public void setFileStreamRetention(Duration fileStreamRetention) {
        this.fileStreamRetention = fileStreamRetention;
    }

    public int sendTimeLimitMillis() {
        if (ObjectUtil.isNull(sendTimeLimit)) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.send-time-limit must not be null");
        }
        long millis = durationMillis(
                sendTimeLimit, "nnm.bot.channels.onebot.send-time-limit");
        if (millis <= 0L || millis > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.send-time-limit must be between 1ms and "
                            + Integer.MAX_VALUE + "ms");
        }
        return (int) millis;
    }

    public long fileStreamRetentionMillis() {
        if (ObjectUtil.isNull(fileStreamRetention)) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-stream-retention must not be null");
        }
        long millis = durationMillis(
                fileStreamRetention, "nnm.bot.channels.onebot.file-stream-retention");
        if (millis <= 0L) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-stream-retention must be positive");
        }
        return millis;
    }

    public void validateDefaults() {
        if (ObjectUtil.isNull(probeTimeout) || probeTimeout.isZero() || probeTimeout.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.probe-timeout must be positive");
        }
        sendTimeLimitMillis();
        if (sendBufferSizeBytes <= 0) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.send-buffer-size-bytes must be positive");
        }
        if (ObjectUtil.isNull(fileTransferMode)) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-transfer-mode must not be null");
        }
        if (ObjectUtil.isNull(fileStagingDirectory)) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-staging-directory must not be null");
        }
        if (fileStreamChunkSizeBytes <= 0) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-stream-chunk-size-bytes must be positive");
        }
        long encodedChunkBytes = ((long) fileStreamChunkSizeBytes + 2L) / 3L * 4L;
        if (encodedChunkBytes + 64L * 1024L > sendBufferSizeBytes) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-stream-chunk-size-bytes exceeds "
                            + "the configured WebSocket send buffer");
        }
        if (fileStreamMaxBytes <= 0L) {
            throw new IllegalArgumentException(
                    "nnm.bot.channels.onebot.file-stream-max-bytes must be positive");
        }
        fileStreamRetentionMillis();
    }

    private static long durationMillis(Duration duration, String propertyName) {
        try {
            return duration.toMillis();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(propertyName + " is too large", exception);
        }
    }

}
