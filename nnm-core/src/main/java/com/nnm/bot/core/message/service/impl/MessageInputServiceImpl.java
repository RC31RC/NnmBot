package com.nnm.bot.core.message.service.impl;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotMessageReadProvider;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.message.cache.MessageReferenceCache;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageReference;
import com.nnm.bot.core.message.service.MessageInputService;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** 统一图片来源选择和原接收账号访问校验，不把渠道协议泄漏到 Warframe。 */
@Service
public class MessageInputServiceImpl implements MessageInputService {

    @Resource
    private List<BotMessageReadProvider> providers;
    @Resource
    private MessageReferenceCache references;
    @Resource
    private ClusterBotRuntime cluster;

    /** {@inheritDoc} */
    @Override
    public Optional<BotIncomingAttachment> firstImage(CommandInvocation invocation) {
        BotMessageContext context = invocation.context();
        if (!cluster.isLocalOwner(new BotChannelKey(context.platform(), context.botAccountId()))) {
            throw new BusinessException(BotChannelErrorCode.INSTANCE_DISABLED);
        }
        Optional<BotIncomingAttachment> direct = image(invocation.attachments());
        if (direct.isPresent())
            return Optional.of(resolve(context, direct.get()));
        BotMessageReference reference = invocation.reference();
        if (reference == null)
            return Optional.empty();
        Optional<BotIncomingAttachment> embedded = image(reference.attachments());
        if (embedded.isPresent())
            return Optional.of(resolve(context, embedded.get()));
        Optional<List<BotIncomingAttachment>> cached = references.find(context, reference);
        if (cached.isPresent()) {
            return Optional.of(resolve(context, image(cached.get()).orElseThrow(this::unavailable)));
        }
        BotMessageReadProvider provider = provider(context);
        if (!provider.canReadReference(context))
            throw unavailable();
        return Optional.of(resolve(context, image(provider.readReference(context)).orElseThrow(this::unavailable)));
    }

    /** 第一张图片一经选定不悄悄切换来源。 */
    private Optional<BotIncomingAttachment> image(List<BotIncomingAttachment> values) {
        return values.stream().filter(BotIncomingAttachment::isImage).findFirst();
    }

    /** 只对需要解析文件 ID 的图片调用渠道，不在 getter 中隐藏网络读取。 */
    private BotIncomingAttachment resolve(BotMessageContext context, BotIncomingAttachment attachment) {
        if (attachment.url() != null
            && (attachment.url().startsWith("https://") || attachment.url().startsWith("http://")))
            return attachment;
        return provider(context).resolveAttachment(context, attachment);
    }

    /** 使用原接收平台的 Provider，不经回复路由选择其他账号。 */
    private BotMessageReadProvider provider(BotMessageContext context) {
        return providers.stream().filter(value -> value.platform() == context.platform()).findFirst()
            .orElseThrow(this::unavailable);
    }

    /** 原图不可用属于可由用户重新上传解决的输入失败。 */
    private BusinessException unavailable() {
        return new BusinessException(BotChannelErrorCode.REFERENCED_IMAGE_UNAVAILABLE);
    }
}
