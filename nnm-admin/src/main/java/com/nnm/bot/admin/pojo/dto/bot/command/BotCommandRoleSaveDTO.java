package com.nnm.bot.admin.pojo.dto.bot.command;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

/** 保存命令可用系统角色的请求。 */
public record BotCommandRoleSaveDTO(
        /** 被允许使用命令的角色主键集合。 */
        @NotNull
        Set<Long> roleIds) {
}
