package com.nnm.bot.admin.pojo.dto.system;

import jakarta.validation.constraints.NotNull;

/** 修改用户启用状态的请求。 */
public record UserStatusDTO(
        /** 是否启用用户。 */
        @NotNull
        Boolean enabled) {
}
