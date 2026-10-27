package com.nnm.bot.warframe.persistent.curated.pojo.dto.watch;

import jakarta.validation.constraints.NotNull;

public record WarframeSubscriptionUpdateDTO(
        @NotNull
        Boolean enabled,
        @NotNull
        Boolean notifyEnabled) {
}
