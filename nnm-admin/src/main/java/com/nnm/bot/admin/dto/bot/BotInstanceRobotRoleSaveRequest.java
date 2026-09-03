package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 保存单个机器人实例的能力角色集合。 */
public record BotInstanceRobotRoleSaveRequest(
        /** 机器人实例最终绑定的能力角色主键集合。 */
        @NotNull List<Long> roleIds) {
}
