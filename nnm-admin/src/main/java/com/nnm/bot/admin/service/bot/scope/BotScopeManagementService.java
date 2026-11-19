package com.nnm.bot.admin.service.bot.scope;

import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;

import java.util.List;

/** 管理机器人可处理的群组、频道等作用域及成员。 */
public interface BotScopeManagementService {

    /** 查询已登记作用域。
     * @return 作用域列表
     */
    List<BotScopeDTO> scopes();

    /** 从渠道实例主动发现可用作用域，不自动写入配置。
     * @return 发现结果
     */
    List<BotScopeDiscoveryDTO> discover();

    /** 创建作用域。
     * @param request 作用域资料
     * @return 新作用域主键
     */
    long create(BotScopeCreateDTO request);

    /** 更新作用域资料。
     * @param id 作用域主键
     * @param request 更新内容
     */
    void update(long id, BotScopeUpdateDTO request);

    /** 删除作用域及其成员关系。
     * @param id 作用域主键
     */
    void delete(long id);

    /** 查询作用域成员。
     * @param scopeId 作用域主键
     * @return 成员列表
     */
    List<BotScopeMemberDTO> members(long scopeId);

    /** 更新作用域成员角色和启用状态。
     * @param scopeId 作用域主键
     * @param memberId 成员主键
     * @param request 更新内容
     */
    void updateMember(long scopeId, long memberId, BotScopeMemberUpdateDTO request);

}
