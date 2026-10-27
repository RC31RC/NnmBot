package com.nnm.bot.admin.pojo.dto.bot.command;

import com.nnm.bot.core.plugin.model.PresentationPolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 保存命令配置的请求。 */
public record BotCommandSaveDTO(
        /** 命令显示名称。 */
        String name,
        /** 所属功能域编码。 */
        String gameCode,
        /** 命令说明。 */
        String description,
        /** 是否启用。 */
        @NotNull
        Boolean enabled,
        /** 是否显示在帮助菜单。 */
        @NotNull
        Boolean showInHelp,
        /** 展示策略。 */
        @NotNull
        PresentationPolicy presentation,
        /** 自定义触发词。 */
        @Valid
        List<BotCommandTriggerSaveDTO> triggers,
        /** 冷却秒数。 */
        Long cooldownSeconds,
        /** 是否在支持的平台回复中提及命令触发者。 */
        @NotNull
        Boolean mentionInvoker,
        /** 覆盖权限码。 */
        String permissionOverride,
        /** 群组内最低角色要求：OWNER、ADMIN。 */
        String groupRoleRequired,
        /** 关联的数据库 HTML 模板主键，各视图按该顺序选择首个启用模板。 */
        List<Long> templateIds) {
}
