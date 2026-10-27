package com.nnm.bot.admin.pojo.dto.bot.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 创建逻辑群组的请求。 */
public record BotGroupCreateDTO(
        /** 群组编码。 */
        @NotBlank
        String code,
        /** 群组名称。 */
        @NotBlank
        String name,
        /** 群组说明。 */
        String description,
        /** 是否启用。 */
        @NotNull
        Boolean enabled) {
}
