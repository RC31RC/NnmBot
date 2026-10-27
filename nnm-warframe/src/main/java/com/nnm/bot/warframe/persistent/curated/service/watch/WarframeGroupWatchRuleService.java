package com.nnm.bot.warframe.persistent.curated.service.watch;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeWatchMatchDTO;
import java.util.List;
import java.util.Set;

public interface WarframeGroupWatchRuleService {

    /**
     * 创建新的业务记录。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 游戏资料分组订阅监控规则对象。
     */
    WarframeGroupWatchRuleDTO create(long groupId, long memberId, String watchType, String watchKey, String watchValue);

    /**
     * 判断是否满足停用条件。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    boolean disable(long groupId, long memberId, String watchType, String watchKey, String watchValue);

    /**
     * 查询当前启用的订阅规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findEnabledRules();

    /**
     * 查询当前启用的订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findEnabledRules(long groupId);

    /**
     * 查询当前启用的订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findEnabledRules(long groupId, long memberId);

    /**
     * 在订阅规则锁内认领本轮尚未发送的事件身份，避免重复通知。
     * @param ruleId 规则标识。
     * @param matches 匹配集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    Set<String> claimMatches(long ruleId, List<WarframeWatchMatchDTO> matches);
}
