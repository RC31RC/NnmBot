package com.nnm.bot.admin.pojo.dto.bot.command;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 保存命令系统角色限制的请求。 */
public record BotCommandSystemRoleSaveDTO(
        /** 允许使用该命令的系统角色主键。 */
        @NotNull
        List<Long> roleIds) {
}
