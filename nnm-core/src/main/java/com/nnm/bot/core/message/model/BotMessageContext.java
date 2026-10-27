package com.nnm.bot.core.message.model;

import com.nnm.bot.core.plugin.model.CommandExecutionContext;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 与平台无关的消息上下文，供插件处理消息时使用。
 *
 * <p>上下文同时携带规范化消息和原始消息，插件只依赖平台无关字段；
 * {@code nativeSource} 仅在确实需要读取渠道扩展信息时使用。</p>
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
    private final String dispatchId;
    private final long receivedAt;
    private final CommandExecutionContext execution;

    private BotMessageContext(BotPlatform platform,
                              BotMessageScope scope,
                              String botAccountId,
                              String senderId,
                              String conversationId,
                              String messageId,
                              String rawMessage,
                              String message,
                              Object nativeSource,
                              List<BotIncomingAttachment> attachments) {
        this(platform, scope, botAccountId, senderId, conversationId, messageId,
                rawMessage, message, nativeSource, attachments, null, System.currentTimeMillis(), null);
    }

    private BotMessageContext(BotPlatform platform, BotMessageScope scope, String botAccountId,
                              String senderId, String conversationId, String messageId,
                              String rawMessage, String message, Object nativeSource,
                              List<BotIncomingAttachment> attachments, String dispatchId, long receivedAt, CommandExecutionContext execution) {
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
        this.dispatchId = dispatchId;
        this.receivedAt = receivedAt;
        this.execution = execution;
    }

    public static BotMessageContext of(BotPlatform platform,
                                       BotMessageScope scope,
                                       String botAccountId,
                                       String senderId,
                                       String conversationId,
                                       String messageId,
                                       String rawMessage,
                                       String message,
                                       Object nativeSource) {
        return of(platform, scope, botAccountId, senderId, conversationId, messageId,
                rawMessage, message, nativeSource, List.of());
    }

    public static BotMessageContext of(BotPlatform platform,
                                       BotMessageScope scope,
                                       String botAccountId,
                                       String senderId,
                                       String conversationId,
                                       String messageId,
                                       String rawMessage,
                                       String message,
                                       Object nativeSource,
                                       List<BotIncomingAttachment> attachments) {
        Assert.notNull(platform, "platform must not be null");
        Assert.notNull(scope, "scope must not be null");
        Assert.hasText(botAccountId, "botAccountId must not be blank");
        Assert.hasText(senderId, "senderId must not be blank");
        Assert.hasText(conversationId, "conversationId must not be blank");
        return new BotMessageContext(
                platform,
                scope,
                botAccountId,
                senderId,
                conversationId,
                messageId,
                Objects.requireNonNullElse(rawMessage, ""),
                normalizeMessage(message, rawMessage),
                nativeSource,
                attachments
        );
    }

    private static String normalizeMessage(String message, String rawMessage) {
        String value = StringUtils.hasText(message)
                ? message
                : Objects.requireNonNullElse(rawMessage, "");
        return value.trim();
    }

    public BotMessageContext withDispatchId(String id) {
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId,
                messageId, rawMessage, message, nativeSource, attachments, id, receivedAt, execution);
    }

    public BotMessageContext withReceivedAt(long timestamp) {
        Assert.isTrue(timestamp > 0, "receivedAt must be positive");
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId,
                messageId, rawMessage, message, nativeSource, attachments, dispatchId, timestamp, execution);
    }

    public BotMessageContext withExecution(CommandExecutionContext value) {
        return new BotMessageContext(platform, scope, botAccountId, senderId, conversationId,
                messageId, rawMessage, message, nativeSource, attachments, dispatchId, receivedAt, value);
    }

    public CommandExecutionContext execution() {
        return execution;
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
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SOURCE_INVALID,
                    "消息来源不是 " + type.getSimpleName() + ": " + nativeSource);
        }
        return type.cast(nativeSource);
    }
}
