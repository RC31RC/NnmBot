package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;

/**
 * QQ 官方机器人消息来源的协议适配结果。
 *
 * @param eventType QQ 官方事件类型
 * @param chatType 会话类型
 * @param targetId 会话或群组标识
 * @param messageId 平台消息标识
 * @param appId 当前机器人应用 ID
 * @param rawEvent 平台返回的原始事件 JSON
 */
public record QqOfficialMessageSource(String eventType,
                                      QqOfficialChatType chatType,
                                      String targetId,
                                      String messageId,
                                      String appId,
                                      String rawEvent) {

    public String messagePath() {
        return switch (chatType) {
            case C2C -> "/v2/users/" + targetId + "/messages";
            case GROUP -> "/v2/groups/" + targetId + "/messages";
            case CHANNEL -> "/channels/" + targetId + "/messages";
            case DIRECT -> "/dms/" + targetId + "/messages";
        };
    }

    public String mediaPath() {
        return switch (chatType) {
            case C2C -> "/v2/users/" + targetId + "/files";
            case GROUP -> "/v2/groups/" + targetId + "/files";
            case CHANNEL, DIRECT -> throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL, BotChannelCapability.FILE_MESSAGE,
                    "会话类型 " + chatType);
        };
    }

    public String mediaPreparePath() {
        return switch (chatType) {
            case C2C -> "/v2/users/" + targetId + "/upload_prepare";
            case GROUP -> "/v2/groups/" + targetId + "/upload_prepare";
            case CHANNEL, DIRECT -> throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL, BotChannelCapability.FILE_MESSAGE,
                    "会话类型 " + chatType);
        };
    }

    public String mediaPartFinishPath() {
        return switch (chatType) {
            case C2C -> "/v2/users/" + targetId + "/upload_part_finish";
            case GROUP -> "/v2/groups/" + targetId + "/upload_part_finish";
            case CHANNEL, DIRECT -> throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL, BotChannelCapability.FILE_MESSAGE,
                    "会话类型 " + chatType);
        };
    }

    public boolean supportsMedia() {
        return chatType == QqOfficialChatType.C2C || chatType == QqOfficialChatType.GROUP;
    }

    public boolean requiresMessageType() {
        return chatType == QqOfficialChatType.C2C || chatType == QqOfficialChatType.GROUP;
    }
}
