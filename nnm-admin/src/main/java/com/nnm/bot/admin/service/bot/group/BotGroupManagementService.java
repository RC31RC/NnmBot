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
import com.nnm.bot.core.task.service.TaskProgressReporter;
import com.nnm.bot.core.channel.port.BotGroupMemberEventSink;

import java.util.List;

/** 管理机器人群组、绑定关系、成员资料和主动发现流程。 */
public interface BotGroupManagementService extends BotGroupMemberEventSink {

    /** 查询已登记群组。
     * @return 群组列表
     */
    List<BotGroupDTO> list();

    /** 从当前渠道实例主动发现可管理的作用域。
     * @return 发现结果，不自动创建作用域
     */
    List<BotScopeDiscoveryDTO> discover();

    /** 默认委托不带进度回调的发现实现；具体服务可覆盖以报告进度。
     * @param reporter 任务进度接收器
     * @return 发现结果，不自动创建作用域
     */
    default List<BotScopeDiscoveryDTO> discover(TaskProgressReporter reporter) {
        return discover();
    }

    /** 主动发现并同步已登记群组的成员信息。
     * @return 发现及同步结果
     */
    List<BotScopeDiscoveryDTO> discoverAndSynchronize();

    /** 默认委托不带进度回调的同步实现；具体服务可覆盖以报告进度。
     * @param reporter 任务进度接收器
     * @return 发现及同步结果
     */
    default List<BotScopeDiscoveryDTO> discoverAndSynchronize(TaskProgressReporter reporter) {
        return discoverAndSynchronize();
    }

    /** 刷新指定群组的成员列表。
     * @param groupId 群组主键
     */
    void refreshMembers(long groupId);

    /** 默认委托不带进度回调的成员刷新；具体服务可覆盖以报告进度。
     * @param groupId 群组主键
     * @param reporter 任务进度接收器
     */
    default void refreshMembers(long groupId, TaskProgressReporter reporter) {
        refreshMembers(groupId);
    }

    /** 创建群组。
     * @param request 群组资料
     * @return 新群组主键
     */
    long create(BotGroupCreateDTO request);

    /** 预览群组修改对机器人绑定的影响。
     * @param id 群组主键
     * @param request 拟更新内容
     * @return 修改影响摘要
     */
    BotGroupBindingPreviewDTO previewGroupUpdate(long id, BotGroupUpdateDTO request);

    /** 更新群组资料。
     * @param id 群组主键
     * @param request 更新内容
     */
    void update(long id, BotGroupUpdateDTO request);

    /** 删除群组及其关联资料。
     * @param id 群组主键
     */
    void delete(long id);

    /** 预览向群组添加机器人绑定的影响。
     * @param groupId 群组主键
     * @param request 拟添加的机器人绑定
     * @return 绑定影响摘要
     */
    BotGroupBindingPreviewDTO previewBinding(long groupId, BotGroupBotSaveDTO request);

    /** 预览更新机器人绑定的影响。
     * @param groupId 群组主键
     * @param bindingId 绑定主键
     * @param request 拟更新内容
     * @return 绑定影响摘要
     */
    BotGroupBindingPreviewDTO previewBindingUpdate(long groupId, long bindingId, BotGroupBotUpdateDTO request);

    /** 为群组添加机器人实例绑定。
     * @param groupId 群组主键
     * @param request 绑定内容
     */
    void bindBot(long groupId, BotGroupBotSaveDTO request);

    /** 移除群组的机器人实例绑定。
     * @param groupId 群组主键
     * @param bindingId 绑定主键
     */
    void unbindBot(long groupId, long bindingId);

    /** 更新群组机器人实例绑定。
     * @param groupId 群组主键
     * @param bindingId 绑定主键
     * @param request 更新内容
     */
    void updateBot(long groupId, long bindingId, BotGroupBotUpdateDTO request);

    /** 查询群组成员。
     * @param groupId 群组主键
     * @return 成员列表
     */
    List<BotGroupMemberDTO> members(long groupId);

    /** 更新群组成员资料和状态。
     * @param groupId 群组主键
     * @param memberId 成员主键
     * @param request 更新内容
     */
    void updateMember(long groupId, long memberId, BotGroupMemberUpdateDTO request);

    /** 更新群组成员积分。
     * @param groupId 群组主键
     * @param memberId 成员主键
     * @param request 积分调整内容
     */
    void updateMemberPoints(long groupId, long memberId, BotGroupMemberPointsUpdateDTO request);

}
