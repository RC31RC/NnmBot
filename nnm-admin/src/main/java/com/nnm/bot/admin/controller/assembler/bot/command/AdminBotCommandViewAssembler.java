package com.nnm.bot.admin.controller.assembler.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandTriggerDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandCostVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandRoleVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandTriggerVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotTemplateVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotCommandViewAssembler {
    private AdminBotCommandViewAssembler() {}

    public static BotCommandCostVO toBotCommandCostVO(BotCommandCostDTO value) {
        if (value == null)
            return null;
        return new BotCommandCostVO(value.commandCode(), value.costPoints(), value.enabled(), value.rewardPoints(),
            value.rewardEnabled());
    }

    public static BotCommandVO toBotCommandVO(BotCommandDTO value) {
        if (value == null)
            return null;
        return new BotCommandVO(value.id(), value.isNew(), value.code(), value.name(), value.gameCode(),
            value.description(), value.matchType(), value.enabled(), value.showInHelp(),
            value.triggers() == null ? null
                : value.triggers().stream().map(AdminBotCommandViewAssembler::toBotCommandTriggerVO).toList(),
            value.cooldownSeconds(), value.mentionInvoker(), value.permissionOverride(), value.groupRoleRequired(),
            value.templateId(), value.createdAt(), value.updatedAt());
    }

    public static List<BotCommandVO> toBotCommandVO(List<BotCommandDTO> values) {
        return values.stream().map(AdminBotCommandViewAssembler::toBotCommandVO).toList();
    }

    public static BotCommandRoleVO toBotCommandRoleVO(BotCommandRoleDTO value) {
        if (value == null)
            return null;
        return new BotCommandRoleVO(value.roleId(), value.roleCode(), value.roleName(), value.enabled(),
            value.assigned());
    }

    public static List<BotCommandRoleVO> toBotCommandRoleVO(List<BotCommandRoleDTO> values) {
        return values.stream().map(AdminBotCommandViewAssembler::toBotCommandRoleVO).toList();
    }

    public static BotCommandTriggerVO toBotCommandTriggerVO(BotCommandTriggerDTO value) {
        if (value == null)
            return null;
        return new BotCommandTriggerVO(value.name(), value.boundaryPolicy());
    }

    public static BotTemplateVO toBotTemplateVO(BotTemplateDTO value) {
        if (value == null)
            return null;
        return new BotTemplateVO(value.id(), value.templateCode(), value.name(), value.commandId(), value.sortOrder(),
            value.htmlContent(), value.enabled(), value.commandCount(), value.createdAt(), value.updatedAt());
    }

    public static List<BotTemplateVO> toBotTemplateVO(List<BotTemplateDTO> values) {
        return values.stream().map(AdminBotCommandViewAssembler::toBotTemplateVO).toList();
    }

}
