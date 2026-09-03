package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.CommandOutputMode;

import java.time.LocalDateTime;
import java.util.List;

/** 管理端展示的命令配置。 */
public record BotCommandItem(
        /** 命令数据库主键。 */
        Long id,
        /** 代码中存在但尚未写入命令配置表。 */
        boolean isNew,
        /** 稳定命令编码。 */
        String code,
        /** 命令显示名称。 */
        String name,
        /** 所属功能域编码。 */
        String gameCode,
        /** 命令说明。 */
        String description,
        /** 是否启用。 */
        boolean enabled,
        /** 是否显示在帮助菜单。 */
        boolean showInHelp,
        /** 默认输出方式。 */
        CommandOutputMode outputMode,
        /** 命令触发词。 */
        List<BotCommandTriggerItem> triggers,
        /** 冷却秒数。 */
        long cooldownSeconds,
        /** 是否在支持的平台回复中提及命令触发者。 */
        boolean mentionInvoker,
        /** 覆盖权限码。 */
        String permissionOverride,
        /** 群组内最低角色要求。 */
        String groupRoleRequired,
        /** 关联模板主键，按默认渲染顺序排列。 */
        List<Long> templateIds,
        /** 关联模板编码，按默认渲染顺序排列。 */
        List<String> templateCodes,
        /** 创建时间。 */
        LocalDateTime createdAt,
        /** 最后更新时间。 */
        LocalDateTime updatedAt) {
}
