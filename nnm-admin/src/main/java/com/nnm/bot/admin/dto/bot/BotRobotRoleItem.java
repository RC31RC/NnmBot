package com.nnm.bot.admin.dto.bot;

import java.time.LocalDateTime;
import java.util.List;

/** 机器人能力角色展示对象。 */
public record BotRobotRoleItem(
        /** 能力角色主键。 */
        Long id,
        /** 能力角色编码。 */
        String code,
        /** 能力角色名称。 */
        String name,
        /** 能力角色说明。 */
        String description,
        /** 是否启用。 */
        boolean enabled,
        /** 已绑定该角色的实例。 */
        List<ListItemBinding> instances,
        /** 该角色拥有的命令。 */
        List<CommandBinding> commands,
        /** 创建时间。 */
        LocalDateTime createdAt,
        /** 最后更新时间。 */
        LocalDateTime updatedAt) {

    /** 能力角色与实例的绑定。 */
    public record ListItemBinding(
            /** 机器人实例主键。 */
            Long botInstanceId,
            /** 机器人实例编码。 */
            String instanceId,
            /** 机器人实例名称。 */
            String displayName) {
    }

    /** 能力角色与命令的绑定。 */
    public record CommandBinding(
            /** 命令编码。 */
            String code,
            /** 命令名称。 */
            String name,
            /** 命令所属功能域。 */
            String gameCode) {
    }
}
