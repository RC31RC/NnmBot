package com.nnm.bot.core.qqofficial.message;

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
            case CHANNEL, DIRECT -> throw new UnsupportedOperationException(
                    "QQ official media upload is not supported for " + chatType);
        };
    }

    public boolean supportsMedia() {
        return chatType == QqOfficialChatType.C2C || chatType == QqOfficialChatType.GROUP;
    }

    public boolean requiresMessageType() {
        return chatType == QqOfficialChatType.C2C || chatType == QqOfficialChatType.GROUP;
    }
}
