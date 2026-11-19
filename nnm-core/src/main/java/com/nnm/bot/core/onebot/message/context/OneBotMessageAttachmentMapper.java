package com.nnm.bot.core.onebot.message.context;

import com.mikuac.shiro.model.ArrayMsg;
import com.nnm.bot.core.messaging.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.messaging.model.BotIncomingAttachment;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** 将 OneBot 消息段中的文件字段转换为平台无关附件。 */
public final class OneBotMessageAttachmentMapper {

    private OneBotMessageAttachmentMapper() {}

    public static List<BotIncomingAttachment> from(List<ArrayMsg> messageSegments) {
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

    private static BotIncomingAttachment toAttachment(ArrayMsg segment, BotIncomingAttachmentType type) {
        String url = firstText(segment, "url", "file");
        if (url != null && !url.startsWith("https://") && !url.startsWith("http://"))
            url = null;
        String fileId = firstText(segment, "file_id", "file_unique", "file");
        String fileName = firstText(segment, "name", "file");
        String contentType = firstText(segment, "content_type", "mime", "mime_type");
        if (StrUtil.isBlank(contentType) && type == BotIncomingAttachmentType.IMAGE) {
            contentType = contentTypeFromName(fileName);
        }
        Map<String, String> metadata =
            Map.of("platformType", type == BotIncomingAttachmentType.IMAGE ? "image" : "file", "rawType",
                Objects.requireNonNullElse(segment.getRawType(), "unknown"));
        return new BotIncomingAttachment(type, fileName, contentType, url, fileId,
            positiveLong(segment.getLongData("size")), positiveInt(segment.getLongData("width")),
            positiveInt(segment.getLongData("height")), metadata);
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
        if (lower.endsWith(".png"))
            return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return "image/jpeg";
        if (lower.endsWith(".webp"))
            return "image/webp";
        if (lower.endsWith(".gif"))
            return "image/gif";
        return null;
    }

    private static Long positiveLong(long value) {
        return value > 0 ? value : null;
    }

    private static Integer positiveInt(long value) {
        return value > 0 && value <= Integer.MAX_VALUE ? (int)value : null;
    }
}
