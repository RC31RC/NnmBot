package com.nnm.bot.admin.pojo.dto.bot.group;

import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Move;
import java.util.List;

public record BotGroupBindingPreviewDTO(boolean requiresConfirmation, String confirmationToken, List<Move> moves) {
    public static BotGroupBindingPreviewDTO unchanged() {
        return new BotGroupBindingPreviewDTO(false, null, List.of());
    }
}
