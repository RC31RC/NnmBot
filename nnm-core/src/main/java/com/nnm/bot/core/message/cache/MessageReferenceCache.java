package com.nnm.bot.core.message.cache;

import com.nnm.bot.core.config.MessageReadProperties;
import com.nnm.bot.core.constant.BotMessageConstants.ReferenceKind;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageReference;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.stereotype.Component;

/** 同账号同会话的有界附件摘要缓存；原图片不命中命令时也可登记，绝不提前下载。 */
@Component
public class MessageReferenceCache {

    @Resource
    private MessageReadProperties properties;
    /** 以完整来源隔离，REFIDX 与普通 ID 不共用命名空间。 */
    private Cache<Key, List<BotIncomingAttachment>> entries;

    /** 按配置创建缓存；过期和容量淘汰只影响引用可用性，不触发历史查询。 */
    @PostConstruct
    public void initialize() {
        entries = new Cache2kBuilder<Key, List<BotIncomingAttachment>>() {}.entryCapacity(properties.capacity())
            .strictEviction(true)
            // sharpExpiry 只对显式 ExpiryPolicy 生效，单独 expireAfterWrite 仍有定时器延迟。
            .expiryPolicy(
                (key, value, startTime, previous) -> Math.addExact(startTime, properties.retention().toMillis()))
            .sharpExpiry(true).build();
    }

    /** 登记本条附件引用；无附件、摘要超限或缺少索引时不保留。 */
    public void observe(BotMessageContext context) {
        List<BotIncomingAttachment> attachments = context.attachments();
        if (attachments.isEmpty() || attachments.size() > properties.maxAttachments())
            return;
        long characters = attachments.stream().mapToLong(value -> value.toString().length()).sum();
        if (characters > properties.maxMetadataCharacters())
            return;
        remember(context, ReferenceKind.MESSAGE_ID, context.messageId(), attachments);
        remember(context, ReferenceKind.REFERENCE_INDEX, context.referenceIndex(), attachments);
    }

    /** 返回当前来源可访问的引用附件；缓存未命中与未知来源不跨账号回退。 */
    public Optional<List<BotIncomingAttachment>> find(BotMessageContext context, BotMessageReference reference) {
        return Optional.ofNullable(entries.peek(key(context, reference.kind(), reference.id())));
    }

    /** 只登记有实际索引的别名，最多每条消息两个索引。 */
    private void remember(BotMessageContext context, ReferenceKind kind, String id,
        List<BotIncomingAttachment> attachments) {
        if (id != null && !id.isBlank() && id.length() <= properties.maxMetadataCharacters()) {
            entries.put(key(context, kind, id), List.copyOf(attachments));
        }
    }

    /** 不拼接可歧义的裸 ID，来源与会话是缓存身份的一部分。 */
    private Key key(BotMessageContext context, ReferenceKind kind, String id) {
        return new Key(context.platform().name(), context.botAccountId(), context.scope().name(),
            context.conversationId(), kind, id);
    }

    /** 关闭组件拥有的缓存，重复关闭由 cache2k 保证安全。 */
    @PreDestroy
    public void close() {
        if (entries != null)
            entries.close();
    }

    /** 内部缓存键，只服务来源隔离，不作为跨渠道命令去重键。 */
    private record Key(String platform, String account, String scope, String conversation, ReferenceKind kind,
        String id) {
    }
}
