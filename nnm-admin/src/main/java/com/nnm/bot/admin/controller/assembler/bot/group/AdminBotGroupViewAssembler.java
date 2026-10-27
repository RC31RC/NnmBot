package com.nnm.bot.admin.controller.assembler.bot.group;

import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBindingPreviewDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberDTO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupBindingPreviewVO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupMemberVO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotGroupViewAssembler {
    private AdminBotGroupViewAssembler() {
    }

    public static BotGroupBindingPreviewVO toBotGroupBindingPreviewVO(BotGroupBindingPreviewDTO value) {
        if (value == null) return null;
        return new BotGroupBindingPreviewVO(
                value.requiresConfirmation(),
                value.confirmationToken(),
                value.moves());
    }

    public static BotGroupVO toBotGroupVO(BotGroupDTO value) {
        if (value == null) return null;
        return new BotGroupVO(
                value.id(),
                value.code(),
                value.name(),
                value.description(),
                value.enabled(),
                value.memberCount(),
                value.bots() == null ? null : value.bots().stream().map(AdminBotGroupViewAssembler::toBotGroupDTOBotBindingVO).toList(),
                value.createdAt(),
                value.updatedAt());
    }

    public static List<BotGroupVO> toBotGroupVO(List<BotGroupDTO> values) {
        return values.stream().map(AdminBotGroupViewAssembler::toBotGroupVO).toList();
    }

    public static BotGroupVO.BotBindingVO toBotGroupDTOBotBindingVO(BotGroupDTO.BotBindingDTO value) {
        if (value == null) return null;
        return new BotGroupVO.BotBindingVO(
                value.id(),
                value.botInstanceId(),
                value.instanceId(),
                value.displayName(),
                value.platform(),
                value.scopeType(),
                value.scopeKey(),
                value.scopeDisplayName(),
                value.enabled(),
                value.replyPriority(),
                value.runtimeStatus(),
                value.runtimeStatusChangedAt(),
                value.lastError());
    }

    public static BotGroupMemberVO toBotGroupMemberVO(BotGroupMemberDTO value) {
        if (value == null) return null;
        return new BotGroupMemberVO(
                value.id(),
                value.qqId(),
                value.memberOpenid(),
                value.kookId(),
                value.qqIdVerified(),
                value.qqBoundAt(),
                value.displayName(),
                value.memberRole(),
                value.enabled(),
                value.points(),
                value.totalEarned(),
                value.totalSpent());
    }

    public static List<BotGroupMemberVO> toBotGroupMemberVO(List<BotGroupMemberDTO> values) {
        return values.stream().map(AdminBotGroupViewAssembler::toBotGroupMemberVO).toList();
    }

}
