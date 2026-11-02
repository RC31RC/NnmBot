package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 引用缓存只保留附件摘要，不保存图像内容；容量与有效期可按部署覆盖。
 *
 * @param retention 元数据有效期，精度为毫秒
 * @param capacity 缓存索引总容量，一条消息最多占用两个索引
 * @param maxAttachments 单条消息可缓存的最大附件数
 * @param maxMetadataCharacters 单条附件摘要的总字符预算
 */
@ConfigurationProperties("nnm.message.read")
public record MessageReadProperties(@DefaultValue("30m") Duration retention, @DefaultValue("4096") int capacity,
    @DefaultValue("8") int maxAttachments, @DefaultValue("8192") int maxMetadataCharacters) {

    public MessageReadProperties {
        if (retention == null || retention.isNegative() || retention.toMillis() < 1 || capacity < 1
            || maxAttachments < 1 || maxMetadataCharacters < 1) {
            throw new IllegalArgumentException("消息引用缓存有效期、容量和摘要大小必须为正数");
        }
    }
}
