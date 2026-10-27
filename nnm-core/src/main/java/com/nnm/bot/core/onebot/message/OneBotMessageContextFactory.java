package com.nnm.bot.core.onebot.message;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 将 OneBot SDK 事件复制为稳定的核心消息模型。
 *
 * <p>命令层只依赖 {@link BotMessageContext}；原始 SDK 对象仅保存在 native source 中，
 * 供最终发送响应时定位机器人实例和原消息。</p>
 */
public final class OneBotMessageContextFactory {

    private OneBotMessageContextFactory() {
    }

    /**
     * 归一化会话标识、纯文本和附件，不执行命令匹配或外部 I/O。
     */
    public static BotMessageContext from(Bot bot, AnyMessageEvent event) {
        Objects.requireNonNull(bot, "bot must not be null");
        Objects.requireNonNull(event, "event must not be null");

        BotMessageScope scope = switch (event.getMessageType()) {
            case "group" -> BotMessageScope.GROUP;
            case "guild" -> BotMessageScope.CHANNEL;
            default -> BotMessageScope.PRIVATE;
        };
        String accountId = String.valueOf(bot.getSelfId());
        String senderId = String.valueOf(Objects.requireNonNullElse(event.getUserId(), 0L));
        String conversationId = scope == BotMessageScope.GROUP
                ? String.valueOf(Objects.requireNonNullElse(event.getGroupId(), 0L))
                : senderId;
        String rawMessage = Objects.requireNonNullElse(event.getRawMessage(), "");
        String message = StringUtils.hasText(event.getPlainText()) ? event.getPlainText() : rawMessage;

        return BotMessageContext.of(
                BotPlatform.ONEBOT,
                scope,
                accountId,
                senderId,
                conversationId,
                ObjectUtil.isNull(event.getMessageId()) ? null : String.valueOf(event.getMessageId()),
                rawMessage,
                message,
                new OneBotMessageSource(bot, replyEvent(event)),
                attachments(event)
        );
    }

    private static AnyMessageEvent replyEvent(AnyMessageEvent source) {
        // 发送只需要定位字段；排队不能额外持有完整消息段、Base64 或 SDK sender 对象。
        AnyMessageEvent reply = new AnyMessageEvent();
        reply.setMessageType(source.getMessageType());
        reply.setUserId(source.getUserId());
        reply.setGroupId(source.getGroupId());
        reply.setMessageId(source.getMessageId());
        return reply;
    }

    private static List<BotIncomingAttachment> attachments(AnyMessageEvent event) {
        List<ArrayMsg> messageSegments = event.getArrayMsg();
        if (messageSegments == null || messageSegments.isEmpty()) {
            return List.of();
        }
        List<BotIncomingAttachment> attachments = new ArrayList<>();
        for (ArrayMsg segment : messageSegments) {
            String type = segment.getRawType();
            if ("image".equalsIgnoreCase(type)) {
                attachments.add(toAttachment(segment, BotIncomingAttachmentType.IMAGE));
            } else if ("file".equalsIgnoreCase(type)) {
                attachments.add(toAttachment(segment, BotIncomingAttachmentType.FILE));
            }
        }
        return List.copyOf(attachments);
    }

    private static BotIncomingAttachment toAttachment(
            ArrayMsg segment, BotIncomingAttachmentType type) {
        String url = firstText(segment, "url", "file");
        String fileId = firstText(segment, "file_id", "file_unique");
        String fileName = firstText(segment, "name", "file");
        String contentType = firstText(segment, "content_type", "mime", "mime_type");
        if (StrUtil.isBlank(contentType) && type == BotIncomingAttachmentType.IMAGE) {
            contentType = contentTypeFromName(fileName);
        }
        Map<String, String> metadata = Map.of(
                "platformType", Objects.requireNonNullElse(type == BotIncomingAttachmentType.IMAGE
                        ? "image" : "file", "unknown"),
                "rawType", Objects.requireNonNullElse(segment.getRawType(), "unknown"));
        return new BotIncomingAttachment(
                type,
                fileName,
                contentType,
                url,
                fileId,
                positiveLong(segment.getLongData("size")),
                positiveInt(segment.getLongData("width")),
                positiveInt(segment.getLongData("height")),
                metadata);
    }

    private static String firstText(ArrayMsg segment, String... keys) {
        for (String key : keys) {
            String value = segment.getStringData(key);
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static String contentTypeFromName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return null;
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return null;
    }

    private static Long positiveLong(long value) {
        return value > 0 ? value : null;
    }

    private static Integer positiveInt(long value) {
        return value > 0 && value <= Integer.MAX_VALUE ? (int) value : null;
    }
}
