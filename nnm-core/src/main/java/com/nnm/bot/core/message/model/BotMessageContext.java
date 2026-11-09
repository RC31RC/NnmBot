package com.nnm.bot.core.message.model;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.model.CommandExecutionContext;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.util.Assert;

/**
 * 与平台无关的消息上下文，供插件处理消息时使用。
 *
 * <p>
 * 上下文同时携带规范化消息和原始消息，插件只依赖平台无关字段； {@code nativeSource} 仅在确实需要读取渠道扩展信息时使用。
 * </p>
 */
public final class BotMessageContext {

    /** 消息来源平台。 */
    private final BotPlatform platform;
    /** 消息所在作用域类型。 */
    private final BotMessageScope scope;
    /** 当前处理消息的机器人账号。 */
    private final String botAccountId;
    /** 发起消息的平台侧用户标识。 */
    private final String senderId;
    /** 会话或群组标识。 */
    private final String conversationId;
    /** 平台侧消息标识。 */
    private final String messageId;
    /** 平台原始消息内容。 */
    private final String rawMessage;
    /** 去除命令前缀后供插件使用的消息内容。 */
    private final String message;
    /** 平台原生消息对象。 */
    private final Object nativeSource;
    /** 入站消息中的图片和文件引用。 */
    private final List<BotIncomingAttachment> attachments;
    /** 当前事件中的一层显式引用；没有引用时为空。 */
    private final BotMessageReference reference;
    /** QQ 当前消息自身的引用索引，与平台 messageId 分别保存。 */
    private final String referenceIndex;
    private final String invocationId;
    private final String dispatchId;
    private final long receivedAt;
    private final CommandExecutionContext execution;

    private BotMessageContext(BotPlatform platform, BotMessageScope scope, String botAccountId, String senderId,
        String conversationId, String messageId, String rawMessage, String message, Object nativeSource,
        List<BotIncomingAttachment> attachments) {
        this(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage, message, nativeSource,
            attachments, UUID.randomUUID().toString(), null, System.currentTimeMillis(), null, null, null);
    }

    private BotMessageContext(BotPlatform platform, BotMessageScope scope, String botAccountId, String senderId,
        String conversationId, String messageId, String rawMessage, String message, Object nativeSource,
        List<BotIncomingAttachment> attachments, String invocationId, String dispatchId, long receivedAt,
        CommandExecutionContext execution, BotMessageReference reference, String referenceIndex) {
        this.platform = platform;
        this.scope = scope;
        this.botAccountId = botAccountId;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.rawMessage = rawMessage;
        this.message = message;
        this.nativeSource = nativeSource;
        this.attachments = attachments == null ? List.of() : List.copyOf(attachments);
        this.invocationId = invocationId;
        this.dispatchId = dispatchId;
        this.receivedAt = receivedAt;
        this.execution = execution;
        this.reference = reference;
        this.referenceIndex = referenceIndex;
    }

    public static BotMessageContext of(BotPlatform platform, BotMessageScope scope, String botAccountId,
        String senderId, String conversationId, String messageId, String rawMessage, String message,
        Object nativeSource) {
        return of(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage, message, nativeSource,
            List.of());
    }

    public static BotMessageContext of(BotPlatform platform, BotMessageScope scope, String botAccountId,
        String senderId, String conversationId, String messageId, String rawMessage, String message,
        Object nativeSource, List<BotIncomingAttachment> attachments) {
        Assert.notNull(platform, "platform must not be null");
        Assert.notNull(scope, "scope must not be null");
        Assert.hasText(botAccountId, "botAccountId must not be blank");
        Assert.hasText(senderId, "senderId must not be blank");
        Assert.hasText(conversationId, "conversationId must not be blank");
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId, messageId,
            Objects.requireNonNullElse(rawMessage, ""), normalizeMessage(message), nativeSource, attachments);
    }

    private static String normalizeMessage(String message) {
        return Objects.requireNonNullElse(message, "").trim();
    }

    public BotMessageContext withDispatchId(String id) {
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage,
            message, nativeSource, attachments, invocationId, id, receivedAt, execution, reference, referenceIndex);
    }

    public BotMessageContext withReceivedAt(long timestamp) {
        Assert.isTrue(timestamp > 0, "receivedAt must be positive");
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage,
            message, nativeSource, attachments, invocationId, dispatchId, timestamp, execution, reference,
            referenceIndex);
    }

    public BotMessageContext withExecution(CommandExecutionContext value) {
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage,
            message, nativeSource, attachments, invocationId, dispatchId, receivedAt, value, reference, referenceIndex);
    }

    /** 复制平台引用元数据，不发起网络读取。 */
    public BotMessageContext withReference(BotMessageReference value, String index) {
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId, messageId, rawMessage,
            message, nativeSource, attachments, invocationId, dispatchId, receivedAt, execution, value, index);
    }

    /** 返回显式引用；普通消息为空。 */
    public BotMessageReference reference() {
        return reference;
    }

    /** 返回当前消息自身的渠道引用索引，未提供时为空。 */
    public String referenceIndex() {
        return referenceIndex;
    }

    public CommandExecutionContext execution() {
        return execution;
    }

    /** 本次进入系统的调用身份；无平台消息 ID 时只能保证同一次调用内的重试幂等。 */
    public String invocationId() {
        return invocationId;
    }

    public String dispatchId() {
        return dispatchId;
    }

    public long receivedAt() {
        return receivedAt;
    }

    public BotPlatform platform() {
        return platform;
    }

    public BotMessageScope scope() {
        return scope;
    }

    public String botAccountId() {
        return botAccountId;
    }

    public String senderId() {
        return senderId;
    }

    public String conversationId() {
        return conversationId;
    }

    public String messageId() {
        return messageId;
    }

    public String rawMessage() {
        return rawMessage;
    }

    public String message() {
        return message;
    }

    public List<BotIncomingAttachment> attachments() {
        return attachments;
    }

    public boolean isGroupMessage() {
        return scope == BotMessageScope.GROUP;
    }

    public boolean isPrivateMessage() {
        return scope == BotMessageScope.PRIVATE;
    }

    public boolean isChannelMessage() {
        return scope == BotMessageScope.CHANNEL;
    }

    public <T> T nativeSource(Class<T> type) {
        Assert.notNull(type, "type must not be null");
        if (!type.isInstance(nativeSource)) {
            throw new BusinessException(BotChannelErrorCode.MESSAGE_SOURCE_INVALID,
                "消息来源不是 " + type.getSimpleName() + ": " + nativeSource);
        }
        return type.cast(nativeSource);
    }
}
