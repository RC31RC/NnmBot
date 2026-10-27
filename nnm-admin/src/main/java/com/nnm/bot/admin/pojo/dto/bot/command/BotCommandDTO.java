package com.nnm.bot.admin.pojo.dto.bot.command;

import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.RelatedParameter;
import com.nnm.bot.core.constant.MatchType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/** 管理端展示的命令配置。 */
public record BotCommandDTO(
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
        /** 当前插件的匹配边界；关联导航首版只允许 EXACT 命令。 */
        MatchType matchType,
        /** 是否启用。 */
        boolean enabled,
        /** 是否显示在帮助菜单。 */
        boolean showInHelp,
        /** 展示策略。 */
        PresentationPolicy presentation,
        /** 插件实现提供的视图能力，后台只能在此范围内配置。 */
        List<CommandView> views,
        boolean allowRelatedAction,
        List<RelatedParameter> relatedParameters,
        Set<String> actionContextKeys,
        /** 命令触发词。 */
        List<BotCommandTriggerDTO> triggers,
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
