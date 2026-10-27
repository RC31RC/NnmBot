package com.nnm.bot.admin.pojo.dto.bot.notification;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import java.util.List;

public record BotNotificationPreviewDTO(long groupId, String groupName, String platform, String botAccountId,
                                      String conversationId, boolean feasible, String reason, List<MessageDTO> messages) {
    public record MessageDTO(int logicalMessage, int part, List<String> blocks, String text, List<ActionDTO> actions) {
        public MessageDTO {
            blocks = List.copyOf(blocks);
            actions = List.copyOf(actions);
        }
    }

    public record ActionDTO(String label, String value, Presentation presentation) { }
}
