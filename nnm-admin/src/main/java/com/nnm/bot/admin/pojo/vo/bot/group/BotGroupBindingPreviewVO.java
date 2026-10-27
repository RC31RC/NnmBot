package com.nnm.bot.admin.pojo.vo.bot.group;

import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Move;
import java.util.List;

public record BotGroupBindingPreviewVO(boolean requiresConfirmation, String confirmationToken, List<Move> moves) {
    public static BotGroupBindingPreviewVO unchanged() {
        return new BotGroupBindingPreviewVO(false, null, List.of());
    }
}
