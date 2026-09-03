package com.nnm.bot.admin.adapter.onebot;

import com.mikuac.shiro.annotation.GroupDecreaseHandler;
import com.mikuac.shiro.annotation.GroupIncreaseHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.nnm.bot.admin.service.BotGroupManagementService;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 接收 OneBot 群成员变更通知，维护已经绑定的逻辑群组成员。 */
@Slf4j
@Shiro
@Component
public class OneBotGroupMemberEventHandler {

    @Resource
    private BotGroupManagementService groupManagementService;

    @GroupIncreaseHandler
    public void onIncrease(Bot bot, GroupIncreaseNoticeEvent event) {
        if (event == null || event.getGroupId() == null || event.getUserId() == null) {
            return;
        }
        if (event.getUserId() == bot.getSelfId()) {
            return;
        }
        try {
            BotDiscoveredMember member = queryMember(bot, event.getGroupId(), event.getUserId());
            groupManagementService.handleOneBotMemberJoined(
                    String.valueOf(bot.getSelfId()),
                    event.getGroupId(),
                    member);
        } catch (RuntimeException exception) {
            log.warn("OneBot 入群成员同步失败: groupId={}, userId={}",
                    event.getGroupId(), event.getUserId(), exception);
        }
    }

    @GroupDecreaseHandler
    public void onDecrease(Bot bot, GroupDecreaseNoticeEvent event) {
        if (event == null || event.getGroupId() == null || event.getUserId() == null) {
            return;
        }
        if (event.getUserId() == bot.getSelfId()) {
            return;
        }
        try {
            groupManagementService.handleOneBotMemberLeft(
                    String.valueOf(bot.getSelfId()),
                    event.getGroupId(),
                    event.getUserId());
        } catch (RuntimeException exception) {
            log.warn("OneBot 退群成员同步失败: groupId={}, userId={}",
                    event.getGroupId(), event.getUserId(), exception);
        }
    }

    private BotDiscoveredMember queryMember(Bot bot, long groupId, long userId) {
        try {
            ActionData<GroupMemberInfoResp> response =
                    bot.getGroupMemberInfo(groupId, userId, true);
            GroupMemberInfoResp data = response == null ? null : response.getData();
            if (data != null) {
                return new BotDiscoveredMember(
                        String.valueOf(userId),
                        firstText(data.getCard(), data.getNickname()),
                        normalizeRole(data.getRole()));
            }
        } catch (RuntimeException exception) {
            log.debug("OneBot 入群成员详情查询失败，使用默认成员信息: groupId={}, userId={}",
                    groupId, userId, exception);
        }
        return new BotDiscoveredMember(String.valueOf(userId), null, "MEMBER");
    }

    private String firstText(String first, String second) {
        return first != null && !first.isBlank()
                ? first
                : second == null || second.isBlank() ? null : second;
    }

    private String normalizeRole(String role) {
        return switch (role == null ? "" : role.trim().toLowerCase()) {
            case "owner" -> "OWNER";
            case "admin" -> "ADMIN";
            default -> "MEMBER";
        };
    }
}
