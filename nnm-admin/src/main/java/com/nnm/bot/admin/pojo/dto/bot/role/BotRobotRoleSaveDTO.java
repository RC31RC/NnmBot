package com.nnm.bot.admin.pojo.dto.bot.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 保存机器人能力角色的请求。 */
public record BotRobotRoleSaveDTO(
        /** 能力角色编码。 */
        @NotBlank
        String code,
        /** 能力角色名称。 */
        @NotBlank
        String name,
        /** 能力角色说明。 */
        String description,
        /** 是否启用。 */
        @NotNull
        Boolean enabled,
        /** 要绑定的机器人实例主键。 */
        List<Long> botInstanceIds,
        /** 要授予的命令编码。 */
        List<String> commandCodes) {
}
