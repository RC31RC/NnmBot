package com.nnm.bot.warframe.persistent.curated.service.watch;

import com.nnm.bot.warframe.persistent.curated.pojo.vo.watch.WarframeMemberSubscriptionVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeSubscriptionUpdateDTO;
import java.util.List;

public interface WarframeSubscriptionManagementService {
    /**
     * 查询并返回当前条件下的业务列表。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeMemberSubscriptionVO> list(long groupId, long memberId);

    /**
     * 校验权限与输入后更新成员订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     */
    void update(long groupId, long memberId, long ruleId, WarframeSubscriptionUpdateDTO request);

    /**
     * 校验权限后删除成员订阅规则，并维护关联资源。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     */
    void delete(long groupId, long memberId, long ruleId);
}
