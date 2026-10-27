package com.nnm.bot.admin.pojo.vo.bot.notification;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import java.util.List;

public record BotNotificationPreviewVO(long groupId, String groupName, String platform, String botAccountId,
                                      String conversationId, boolean feasible, String reason, List<MessageVO> messages) {
    public record MessageVO(int logicalMessage, int part, List<String> blocks, String text, List<ActionVO> actions) {
        public MessageVO {
            blocks = List.copyOf(blocks);
            actions = List.copyOf(actions);
        }
    }

    public record ActionVO(String label, String value, Presentation presentation) { }
}
