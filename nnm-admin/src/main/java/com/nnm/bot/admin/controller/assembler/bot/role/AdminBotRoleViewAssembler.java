package com.nnm.bot.admin.controller.assembler.bot.role;

import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleDTO;
import com.nnm.bot.admin.pojo.vo.bot.role.BotRobotRoleVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotRoleViewAssembler {
    private AdminBotRoleViewAssembler() {
    }

    public static BotRobotRoleVO toBotRobotRoleVO(BotRobotRoleDTO value) {
        if (value == null) return null;
        return new BotRobotRoleVO(
                value.id(),
                value.code(),
                value.name(),
                value.description(),
                value.enabled(),
                value.instances() == null ? null : value.instances().stream().map(AdminBotRoleViewAssembler::toBotRobotRoleDTOListItemBindingVO).toList(),
                value.commands() == null ? null : value.commands().stream().map(AdminBotRoleViewAssembler::toBotRobotRoleDTOCommandBindingVO).toList(),
                value.createdAt(),
                value.updatedAt());
    }

    public static List<BotRobotRoleVO> toBotRobotRoleVO(List<BotRobotRoleDTO> values) {
        return values.stream().map(AdminBotRoleViewAssembler::toBotRobotRoleVO).toList();
    }

    public static BotRobotRoleVO.ListItemBindingVO toBotRobotRoleDTOListItemBindingVO(BotRobotRoleDTO.ListItemBindingDTO value) {
        if (value == null) return null;
        return new BotRobotRoleVO.ListItemBindingVO(
                value.botInstanceId(),
                value.instanceId(),
                value.displayName());
    }

    public static BotRobotRoleVO.CommandBindingVO toBotRobotRoleDTOCommandBindingVO(BotRobotRoleDTO.CommandBindingDTO value) {
        if (value == null) return null;
        return new BotRobotRoleVO.CommandBindingVO(
                value.code(),
                value.name(),
                value.gameCode());
    }
}
