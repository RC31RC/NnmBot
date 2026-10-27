package com.nnm.bot.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

/** 持久化身份只有 object key；bucket 和服务地址由当前环境配置决定。 */
public interface ObjectStorage {

    String put(String objectId, InputStream input, long size, String contentType) throws IOException;

    Optional<StoredObject> stat(String objectId);

    InputStream open(String objectId) throws IOException;

    void copy(String sourceId, String targetId);

    void delete(String objectId);

    int deleteExpired(String prefix, Instant cutoff);

    record StoredObject(String id, long size, String contentType) {
    }
}
