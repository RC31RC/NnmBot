package com.nnm.bot.core.message.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * 渠道无关的文件消息。小文件可使用内存字节，大文件使用磁盘路径避免重复复制。
 */
public final class BotFile {

    /** 用户可见文件名。 */
    private final String fileName;
    /** 文件 MIME 类型。 */
    private final String contentType;
    /** 小文件的内存内容，与 path 二选一。 */
    private final byte[] content;
    /** 大文件的本地路径，与 content 二选一。 */
    private final Path path;

    public BotFile(String fileName, String contentType, byte[] content) {
        this(fileName, contentType, content, null);
    }

    public BotFile(String fileName, String contentType, Path path) {
        this(fileName, contentType, null, path);
    }

    private BotFile(String fileName, String contentType, byte[] content, Path path) {
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
                    "file path must be a readable regular file: " + path);
        }
        this.fileName = fileName.trim();
        this.contentType = contentType.trim().toLowerCase();
        this.content = hasContent ? Arrays.copyOf(content, content.length) : null;
        this.path = hasPath ? path.toAbsolutePath().normalize() : null;
    }

    public String fileName() {
        return this.fileName;
    }

    public String contentType() {
        return this.contentType;
    }

    public byte[] content() {
        if (ObjectUtil.isNull(this.content)) {
            throw new IllegalStateException("file content is path-backed");
        }
        return Arrays.copyOf(this.content, this.content.length);
    }

    public Path path() {
        return this.path;
    }

    public boolean pathBacked() {
        return ObjectUtil.isNotNull(this.path);
    }

    public long size() throws IOException {
        return this.pathBacked() ? Files.size(this.path) : this.content.length;
    }

    public InputStream openStream() throws IOException {
        return this.pathBacked()
                ? Files.newInputStream(this.path)
                : new ByteArrayInputStream(this.content);
    }

    public byte[] readAllBytes(long maxBytes) throws IOException {
        if (maxBytes <= 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "maxBytes must be between 1 and " + (Integer.MAX_VALUE - 1));
        }
        long fileSize = this.size();
        if (fileSize > maxBytes) {
            throw new FileSizeLimitExceededException(fileSize, maxBytes);
        }
        if (!this.pathBacked()) {
            return Arrays.copyOf(this.content, this.content.length);
        }
        try (InputStream input = this.openStream()) {
            byte[] bytes = input.readNBytes((int) maxBytes + 1);
            if (bytes.length > maxBytes) {
                throw new FileSizeLimitExceededException(bytes.length, maxBytes);
            }
            return bytes;
        }
    }

    /** 文件超过渠道允许的内存读取上限。 */
    public static final class FileSizeLimitExceededException extends IOException {

        private final long actualBytes;
        private final long maxBytes;

        public FileSizeLimitExceededException(long actualBytes, long maxBytes) {
            super("file size " + actualBytes + " exceeds limit " + maxBytes);
            this.actualBytes = actualBytes;
            this.maxBytes = maxBytes;
        }

        public long actualBytes() {
            return this.actualBytes;
        }

        public long maxBytes() {
            return this.maxBytes;
        }
    }
}
