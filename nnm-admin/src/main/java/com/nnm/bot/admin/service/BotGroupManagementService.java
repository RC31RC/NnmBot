package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotGroupBotSaveRequest;
import com.nnm.bot.admin.dto.bot.BotGroupCreateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberPointsUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupMemberUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeDiscoveryItem;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;

import java.util.List;

public interface BotGroupManagementService {

    List<BotGroupItem> list();

    List<BotScopeDiscoveryItem> discover();

    default List<BotScopeDiscoveryItem> discover(TaskProgressReporter reporter) {
        return discover();
    }

    List<BotScopeDiscoveryItem> discoverAndSynchronize();

    default List<BotScopeDiscoveryItem> discoverAndSynchronize(
            TaskProgressReporter reporter) {
        return discoverAndSynchronize();
    }

    void refreshMembers(long groupId);

    default void refreshMembers(long groupId, TaskProgressReporter reporter) {
        refreshMembers(groupId);
    }

    long create(BotGroupCreateRequest request);

    void update(long id, BotGroupUpdateRequest request);

    void delete(long id);

    void bindBot(long groupId, BotGroupBotSaveRequest request);

    void unbindBot(long groupId, long bindingId);

    List<BotGroupMemberItem> members(long groupId);

    void updateMember(long groupId, long memberId, BotGroupMemberUpdateRequest request);

    void updateMemberPoints(long groupId, long memberId,
                            BotGroupMemberPointsUpdateRequest request);

    void handleOneBotMemberJoined(
            String botAccountId, long groupId, BotDiscoveredMember member);

    void handleOneBotMemberLeft(String botAccountId, long groupId, long userId);
}
