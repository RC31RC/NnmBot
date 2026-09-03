package com.nnm.bot.core.onebot.discovery;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupInfoResp;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotDiscoveredScope;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotPlatform;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OneBot 群组发现。仅查询在线 Bot，不会自动创建作用域或系统用户。
 */
@Component
@Slf4j
public class OneBotScopeDiscoveryProvider implements BotScopeDiscoveryProvider {

    @Resource
    private BotContainer botContainer;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        long accountId;
        try {
            accountId = Long.parseLong(instance.botAccountId());
        } catch (NumberFormatException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.INVALID_ACCOUNT_ID,
                    "OneBot 账号不是有效的 QQ 号: " + instance.botAccountId(),
                    exception);
        }
        Bot bot = this.botContainer.robots.get(accountId);
        if (ObjectUtil.isNull(bot)) {
            return BotScopeDiscoveryResult.unsupported("机器人当前未连接");
        }
        try {
            ActionList<GroupInfoResp> response = bot.getGroupList();
            if (ObjectUtil.isNull(response) || ObjectUtil.isNull(response.getData())) {
                return BotScopeDiscoveryResult.unsupported("OneBot 未返回群组列表");
            }
            List<BotDiscoveredScope> scopes = response.getData().stream()
                    .filter(group -> ObjectUtil.isNotNull(group.getGroupId()))
                    .map(group -> {
                        MemberDiscovery members = loadMembers(bot, group.getGroupId());
                        return new BotDiscoveredScope(
                                "GROUP",
                                String.valueOf(group.getGroupId()),
                                firstText(group.getGroupName(), group.getGroupMemo()),
                                group.getMemberCount(),
                                members.members(),
                                members.complete());
                    })
                    .toList();
            return new BotScopeDiscoveryResult(true, null, scopes);
        } catch (RuntimeException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.SCOPE_DISCOVERY_FAILED,
                    "OneBot 群组查询失败: " + safeMessage(exception),
                    exception);
        }
    }

    private MemberDiscovery loadMembers(Bot bot, Long groupId) {
        try {
            ActionList<GroupMemberInfoResp> response = bot.getGroupMemberList(groupId);
            if (ObjectUtil.isNull(response)
                    || ObjectUtil.isNull(response.getData())
                    || (StrUtil.isNotBlank(response.getStatus())
                    && !"ok".equalsIgnoreCase(response.getStatus()))) {
                // 空列表是有效的空群；data=null 则表示平台没有给出可用于清理的全量结果。
                return new MemberDiscovery(List.of(), false);
            }
            List<BotDiscoveredMember> members = response.getData().stream()
                    .filter(member -> ObjectUtil.isNotNull(member.getUserId()))
                    .map(member -> new BotDiscoveredMember(
                            String.valueOf(member.getUserId()),
                            firstText(member.getCard(), member.getNickname()),
                            normalizeRole(member.getRole())))
                    .toList();
            return new MemberDiscovery(members, true);
        } catch (RuntimeException exception) {
            // 群列表仍可展示；成员查询失败时禁止按空列表清理已有成员。
            log.warn("OneBot 群成员查询失败，跳过本次成员清理: groupId={}", groupId, exception);
            return new MemberDiscovery(List.of(), false);
        }
    }

    private String normalizeRole(String role) {
        return switch (StrUtil.emptyIfNull(role).toLowerCase()) {
            case "owner" -> "OWNER";
            case "admin" -> "ADMIN";
            default -> "MEMBER";
        };
    }

    private String firstText(String first, String second) {
        return StrUtil.isNotBlank(first)
                ? first
                : StrUtil.isBlank(second) ? null : second;
    }

    private String safeMessage(RuntimeException exception) {
        return StrUtil.isBlank(exception.getMessage()) ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }

    private record MemberDiscovery(List<BotDiscoveredMember> members, boolean complete) {
    }
}
