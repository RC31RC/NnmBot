package com.nnm.bot.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

/** 持久化身份只有 object key；bucket 和服务地址由当前环境配置决定。 */
public interface ObjectStorage {

    /** 上传对象并返回稳定对象标识。
     *
     * @param objectId 相对对象键，不包含 bucket 或服务地址
     * @param input 对象内容流；调用方负责关闭
     * @param size 已知内容长度，单位为字节
     * @param contentType MIME 类型
     * @return 已存储对象标识
     * @throws IOException 上传内容读取失败
     */
    String put(String objectId, InputStream input, long size, String contentType) throws IOException;

    /** 查询对象元数据；对象不存在时返回空。
     *
     * @param objectId 相对对象键
     * @return 对象元数据或空值
     */
    Optional<StoredObject> stat(String objectId);

    /** 打开对象内容流，调用方负责关闭。
     *
     * @param objectId 相对对象键
     * @return 对象内容流
     * @throws IOException 对象内容无法读取
     */
    InputStream open(String objectId) throws IOException;

    /** 仅获取对象指定字节区间，供大预览的索引分页使用。 */
    InputStream openRange(String objectId, long offset, long length) throws IOException;

    /** 复制对象；目标对象按存储服务语义覆盖。
     *
     * @param sourceId 源对象键
     * @param targetId 目标对象键
     */
    void copy(String sourceId, String targetId);

    /** 删除对象；目标不存在时由实现按幂等删除处理。
     *
     * @param objectId 相对对象键
     */
    void delete(String objectId);

    /** 删除前缀下早于指定时点的对象。
     *
     * @param prefix 对象键前缀
     * @param cutoff 保留边界，早于该时点的对象会被删除
     * @return 成功删除的对象数
     */
    int deleteExpired(String prefix, Instant cutoff);

    /** 对象存储返回的最小元数据快照。
     *
     * @param id 对象键
     * @param size 内容长度，单位为字节
     * @param contentType MIME 类型
     */
    record StoredObject(String id, long size, String contentType) {
    }
}
