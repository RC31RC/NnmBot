package com.nnm.bot.common.storage;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import cn.hutool.core.lang.Assert;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
public class MinioObjectStorage implements ObjectStorage {

    @Resource
    private MinioClient objectStorageClient;
    @Resource
    private ObjectStorageProperties properties;
    private Semaphore permits;

    @PostConstruct
    void initialize() {
        permits = new Semaphore(properties.getMaxConcurrentRequests(), true);
    }

    @Override
    public String put(String objectId, InputStream input, long size, String contentType)
            throws IOException {
        validateId(objectId);
        Assert.notNull(input, "Object input is required");
        Assert.isTrue(size >= 0, "Object size cannot be negative");
        return execute("put", () -> {
            // 限制 multipart part 为 5 MiB，避免 SDK 对大文件使用过大的内存缓冲。
            objectStorageClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket()).object(objectId)
                    .stream(input, size, 5L * 1024 * 1024)
                    .contentType(contentType).build());
            return objectId;
        });
    }

    @Override
    public Optional<StoredObject> stat(String objectId) {
        validateId(objectId);
        return execute("stat", () -> {
            try {
                StatObjectResponse result = objectStorageClient.statObject(StatObjectArgs.builder()
                        .bucket(properties.getBucket()).object(objectId).build());
                return Optional.of(new StoredObject(objectId, result.size(), result.contentType()));
            } catch (ErrorResponseException exception) {
                // bucket 缺失、403、网络错误不能伪装成附件不存在。
                if ("NoSuchKey".equals(exception.errorResponse().code())
                        || "NoSuchObject".equals(exception.errorResponse().code())) {
                    return Optional.empty();
                }
                throw exception;
            }
        });
    }

    @Override
    public InputStream open(String objectId) throws IOException {
        validateId(objectId);
        acquire();
        try {
            InputStream input = objectStorageClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucket()).object(objectId).build());
            // permit 与响应流同生命周期，不能在返回流时就释放，也不能靠 ThreadLocal 传播。
            return new FilterInputStream(input) {
                private boolean closed;

                @Override
                public void close() throws IOException {
                    if (!closed) {
                        closed = true;
                        try {
                            super.close();
                        } finally {
                            permits.release();
                        }
                    }
                }
            };
        } catch (Exception exception) {
            permits.release();
            throw failure("get", exception);
        }
    }

    @Override
    public void copy(String sourceId, String targetId) {
        validateId(sourceId);
        validateId(targetId);
        execute("copy", () -> {
            objectStorageClient.copyObject(CopyObjectArgs.builder()
                    .bucket(properties.getBucket()).object(targetId)
                    .source(CopySource.builder().bucket(properties.getBucket())
                            .object(sourceId).build()).build());
            return null;
        });
    }

    @Override
    public void delete(String objectId) {
        validateId(objectId);
        execute("delete", () -> {
            objectStorageClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket()).object(objectId).build());
            return null;
        });
    }

    @Override
    public int deleteExpired(String prefix, Instant cutoff) {
        validateId(prefix);
        Assert.isTrue(prefix.endsWith("/"), "Cleanup requires a directory prefix");
        Assert.notNull(cutoff, "Cleanup cutoff is required");
        return execute("cleanup", () -> {
            int deleted = 0;
            // SDK 逐页枚举；删除也在当前 permit 内执行，避免嵌套获取导致死锁。
            for (Result<Item> result : objectStorageClient.listObjects(ListObjectsArgs.builder()
                    .bucket(properties.getBucket()).prefix(prefix).recursive(true).build())) {
                Item item = result.get();
                if (!item.isDir() && item.lastModified().toInstant().isBefore(cutoff)) {
                    objectStorageClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(properties.getBucket()).object(item.objectName()).build());
                    deleted++;
                }
            }
            return deleted;
        });
    }

    static void validateId(String objectId) {
        Assert.isTrue(objectId != null && objectId.length() <= 1024
                        && objectId.matches("[A-Za-z0-9][A-Za-z0-9._/-]*")
                        && !objectId.contains("//"),
                "Object ID must be a relative S3 key, not a URL or filesystem path");
        for (String segment : objectId.split("/")) {
            Assert.isTrue(!".".equals(segment) && !"..".equals(segment),
                    "Object ID cannot contain traversal segments");
        }
    }

    private void acquire() {
        try {
            permits.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw failure("acquire", exception);
        }
    }

    private <T> T execute(String operation, StorageCall<T> call) {
        acquire();
        try {
            return call.run();
        } catch (Exception exception) {
            throw failure(operation, exception);
        } finally {
            permits.release();
        }
    }

    private ObjectStorageException failure(String operation, Exception cause) {
        return new ObjectStorageException("对象存储操作失败: " + operation, cause);
    }

    @FunctionalInterface
    private interface StorageCall<T> {
        T run() throws Exception;
    }
}
