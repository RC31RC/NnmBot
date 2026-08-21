package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Size;

public record OneBotAccountProbeRequest(
        Long instanceId,
        @Size(max = 500) String connectionUrl,
        @Size(max = 1000) String accessToken) {
}
