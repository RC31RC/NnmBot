package com.nnm.bot.admin.pojo.dto.bot.scope;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 修改平台作用域成员关系的请求。 */
public record BotScopeMemberUpdateDTO(
        /** 成员角色。 */
        @NotBlank
        String memberRole,
        /** 是否启用。 */
        @NotNull
        Boolean enabled) {
}
