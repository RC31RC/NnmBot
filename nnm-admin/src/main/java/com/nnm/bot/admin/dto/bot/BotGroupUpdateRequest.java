package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 修改逻辑群组的请求。 */
public record BotGroupUpdateRequest(
        /** 群组名称。 */
        @NotBlank String name,
        /** 群组说明。 */
        String description,
        /** 是否启用。 */
        @NotNull Boolean enabled) {
}
