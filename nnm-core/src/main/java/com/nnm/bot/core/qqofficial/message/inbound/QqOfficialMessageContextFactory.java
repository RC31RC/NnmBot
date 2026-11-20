package com.nnm.bot.core.qqofficial.message.inbound;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.collection.CollUtil;

import com.nnm.bot.core.qqofficial.message.context.QqOfficialMessageSource;
import com.nnm.bot.core.qqofficial.message.context.QqOfficialMessageAttachmentMapper;
import com.nnm.bot.core.messaging.constant.BotMessageConstants.ReferenceKind;
import com.nnm.bot.core.messaging.constant.BotMessageScope;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.qqofficial.constant.QqOfficialChatType;
import com.nnm.bot.core.messaging.model.BotIncomingAttachment;
import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.messaging.model.BotMessageReference;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayEventDTO;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayEventDTO.ReferencedMessageDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** 将类型化 QQ Gateway 事件转换为平台无关上下文；原始 JSON 用于源协议追踪。 */
public final class QqOfficialMessageContextFactory {
    private static final int REFERENCE_MESSAGE_TYPE = 103;

    private QqOfficialMessageContextFactory() {}

    public static Optional<BotMessageContext> from(String appId, String eventType, QqOfficialGatewayEventDTO event,
        String rawJson) {
        QqOfficialChatType chatType = chatType(eventType);
        if (chatType == null || event == null)
            return Optional.empty();
        var author = event.author();
        String senderId = switch (chatType) {
            case C2C -> author == null ? null : text(author.userOpenid());
            case GROUP -> author == null ? null : firstText(author.memberOpenid(), author.userOpenid());
            case CHANNEL, DIRECT -> author == null ? null : text(author.id());
        };
        String targetId = switch (chatType) {
            case C2C -> senderId;
            case GROUP -> text(event.groupOpenid());
            case CHANNEL -> text(event.channelId());
            case DIRECT -> text(event.guildId());
        };
        String messageId = text(event.id());
        if (CharSequenceUtil.isBlank(appId) || senderId == null || targetId == null || messageId == null)
            return Optional.empty();
        String content = normalizeContent(event.content() == null ? "" : event.content(), appId);
        BotMessageScope scope = switch (chatType) {
            case GROUP -> BotMessageScope.GROUP;
            case CHANNEL -> BotMessageScope.CHANNEL;
            case C2C, DIRECT -> BotMessageScope.PRIVATE;
        };
        String timestamp = text(event.timestamp());
        return Optional.of(BotMessageContext
            .of(BotPlatform.QQ_OFFICIAL, scope, appId, senderId, targetId, messageId, rawJson, content,
                new QqOfficialMessageSource(eventType, chatType, targetId, messageId, appId, rawJson,
                    timestamp == null ? null : OffsetDateTime.parse(timestamp).toInstant()),
                QqOfficialMessageAttachmentMapper.from(event))
            .withReference(reference(event), index(event, "msg_idx")));
    }

    static BotMessageReference reference(QqOfficialGatewayEventDTO event) {
        String id = index(event, "ref_msg_idx");
        ReferencedMessageDTO element = CollUtil.isEmpty(event.msgElements()) ? null : event.msgElements().getFirst();
        if (id == null && Integer.valueOf(REFERENCE_MESSAGE_TYPE).equals(event.messageType()) && element != null)
            id = text(element.msgIdx());
        if (id != null) {
            List<BotIncomingAttachment> referenced =
                element != null && (text(element.msgIdx()) == null || id.equals(text(element.msgIdx())))
                    ? QqOfficialMessageAttachmentMapper.from(element.attachments()) : List.of();
            return new BotMessageReference(ReferenceKind.REFERENCE_INDEX, id, referenced);
        }
        String messageId = event.messageReference() == null ? null : text(event.messageReference().messageId());
        return messageId == null ? null : new BotMessageReference(ReferenceKind.MESSAGE_ID, messageId, List.of());
    }

    private static String index(QqOfficialGatewayEventDTO event, String key) {
        List<String> ext = event.messageScene() == null ? null : event.messageScene().ext();
        if (ext == null)
            return null;
        for (String entry : ext) {
            if (entry == null)
                continue;
            int separator = entry.indexOf('=');
            if (separator > 0 && key.equals(CharSequenceUtil.trim(entry.substring(0, separator)))) {
                String id = CharSequenceUtil.trim(entry.substring(separator + 1));
                if (CharSequenceUtil.isNotEmpty(id))
                    return id;
            }
        }
        return null;
    }

    static QqOfficialChatType chatType(String eventType) {
        return switch (eventType) {
            case "C2C_MESSAGE_CREATE" -> QqOfficialChatType.C2C;
            case "GROUP_AT_MESSAGE_CREATE", "GROUP_MESSAGE_CREATE" -> QqOfficialChatType.GROUP;
            case "AT_MESSAGE_CREATE" -> QqOfficialChatType.CHANNEL;
            case "DIRECT_MESSAGE_CREATE" -> QqOfficialChatType.DIRECT;
            default -> null;
        };
    }

    private static String text(String value) {
        return CharSequenceUtil.isBlank(value) ? null : value;
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (text(value) != null)
                return value;
        }
        return null;
    }

    private static String normalizeContent(String content, String appId) {
        if (CharSequenceUtil.isBlank(content) || CharSequenceUtil.isBlank(appId))
            return content;
        return content.replace("<@!" + appId + ">", "").replace("<@" + appId + ">", "");
    }

}
