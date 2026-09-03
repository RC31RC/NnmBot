package com.nnm.bot.core.plugin.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.message.model.BotFile;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

/**
 * 命令产生的附件。插件只描述文件；由响应分发器统一清理其拥有的临时目录。
 */
public final class CommandAttachment implements AutoCloseable {

    /** 用户可见文件名。 */
    private final String fileName;
    /** 文件 MIME 类型。 */
    private final String contentType;
    /** 小附件的内存内容。 */
    private final byte[] content;
    /** 大附件的本地文件路径。 */
    private final Path path;
    /** 发送结束后递归清理的临时目录。 */
    private final Path cleanupRoot;

    public CommandAttachment(String fileName, String contentType, byte[] content) {
        this(fileName, contentType, content, null, null);
    }

    private CommandAttachment(String fileName, String contentType, byte[] content,
                              Path path, Path cleanupRoot) {
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        if (StrUtil.isBlank(contentType)) {
            throw new IllegalArgumentException("contentType must not be blank");
        }
        boolean hasContent = ArrayUtil.isNotEmpty(content);
        boolean hasPath = ObjectUtil.isNotNull(path);
        if (hasContent == hasPath) {
            throw new IllegalArgumentException("exactly one of content or path is required");
        }
        if (hasPath && (!Files.isRegularFile(path) || !Files.isReadable(path))) {
            throw new IllegalArgumentException(
                    "attachment path must be a readable regular file: " + path);
        }
        Path normalizedPath = hasPath ? path.toAbsolutePath().normalize() : null;
        Path normalizedRoot = ObjectUtil.isNull(cleanupRoot)
                ? null
                : cleanupRoot.toAbsolutePath().normalize();
        if (ObjectUtil.isNotNull(normalizedRoot)
                && !normalizedPath.startsWith(normalizedRoot)) {
            throw new IllegalArgumentException("cleanupRoot must contain attachment path");
        }
        this.fileName = fileName.trim();
        this.contentType = contentType.trim().toLowerCase();
        this.content = hasContent ? Arrays.copyOf(content, content.length) : null;
        this.path = normalizedPath;
        this.cleanupRoot = normalizedRoot;
    }

    public static CommandAttachment temporaryFile(String fileName, String contentType,
                                                  Path path, Path cleanupRoot) {
        return new CommandAttachment(fileName, contentType, null, path, cleanupRoot);
    }

    public String fileName() {
        return this.fileName;
    }

    public String contentType() {
        return this.contentType;
    }

    public byte[] content() {
        if (ObjectUtil.isNull(this.content)) {
            throw new IllegalStateException("attachment content is path-backed");
        }
        return Arrays.copyOf(this.content, this.content.length);
    }

    public Path path() {
        return this.path;
    }

    public boolean pathBacked() {
        return ObjectUtil.isNotNull(this.path);
    }

    public BotFile toBotFile() {
        return this.pathBacked()
                ? new BotFile(this.fileName, this.contentType, this.path)
                : new BotFile(this.fileName, this.contentType, this.content);
    }

    @Override
    public void close() {
        if (ObjectUtil.isNull(this.cleanupRoot) || !Files.exists(this.cleanupRoot)) {
            return;
        }
        try {
            Files.walkFileTree(this.cleanupRoot, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException exception)
                        throws IOException {
                    if (ObjectUtil.isNotNull(exception)) {
                        throw exception;
                    }
                    Files.deleteIfExists(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_CLEANUP_FAILED,
                    "清理命令临时文件失败: " + this.cleanupRoot,
                    exception);
        }
    }
}
