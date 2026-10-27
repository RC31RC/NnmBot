package com.nnm.bot.admin.service.bot.group;

import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBindingPreviewDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberPointsUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.channel.port.BotGroupMemberEventSink;

import java.util.List;

public interface BotGroupManagementService extends BotGroupMemberEventSink {

    List<BotGroupDTO> list();

    List<BotScopeDiscoveryDTO> discover();

    default List<BotScopeDiscoveryDTO> discover(TaskProgressReporter reporter) {
        return discover();
    }

    List<BotScopeDiscoveryDTO> discoverAndSynchronize();

    default List<BotScopeDiscoveryDTO> discoverAndSynchronize(
            TaskProgressReporter reporter) {
        return discoverAndSynchronize();
    }

    void refreshMembers(long groupId);

    default void refreshMembers(long groupId, TaskProgressReporter reporter) {
        refreshMembers(groupId);
    }

    long create(BotGroupCreateDTO request);

    BotGroupBindingPreviewDTO previewGroupUpdate(long id, BotGroupUpdateDTO request);

    void update(long id, BotGroupUpdateDTO request);

    void delete(long id);

    BotGroupBindingPreviewDTO previewBinding(long groupId, BotGroupBotSaveDTO request);

    BotGroupBindingPreviewDTO previewBindingUpdate(long groupId, long bindingId, BotGroupBotUpdateDTO request);

    void bindBot(long groupId, BotGroupBotSaveDTO request);

    void unbindBot(long groupId, long bindingId);

    void updateBot(long groupId, long bindingId, BotGroupBotUpdateDTO request);

    List<BotGroupMemberDTO> members(long groupId);

    void updateMember(long groupId, long memberId, BotGroupMemberUpdateDTO request);

    void updateMemberPoints(long groupId, long memberId,
                            BotGroupMemberPointsUpdateDTO request);

}
