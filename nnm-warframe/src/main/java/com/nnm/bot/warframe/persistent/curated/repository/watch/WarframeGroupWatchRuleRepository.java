package com.nnm.bot.warframe.persistent.curated.repository.watch;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.List;
import java.util.Optional;

public interface WarframeGroupWatchRuleRepository
        extends JpaRepository<WarframeGroupWatchRule, Long> {

    /**
     * 按规则主键、群组和成员定位待维护的订阅规则。
     *
     * @param id 目标记录的本地主键。
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeGroupWatchRule> findByIdAndGroupIdAndMemberId(Long id, Long groupId, Long memberId);

    /**
     * 读取全部启用记录的非受管投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findAllProjectedByEnabledTrue();

    /**
     * 读取指定群组全部已启用的订阅规则投影。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findAllProjectedByGroupIdAndEnabledTrue(Long groupId);

    /**
     * 读取指定群组成员全部已启用的订阅规则投影。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findAllProjectedByGroupIdAndMemberIdAndEnabledTrue(
            Long groupId,
            Long memberId);

    /**
     * 读取指定群组成员的全部待维护订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRule> findAllByGroupIdAndMemberId(Long groupId, Long memberId);

    /**
     * 按群组成员及完整订阅条件定位已有规则，供写入时去重。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeGroupWatchRule>
    findFirstByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValue(
            Long groupId, Long memberId, String watchType, String watchKey, String watchValue);

    /**
     * 读取指定成员在给定订阅类型和匹配值下的已启用规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchValue 订阅监控值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRule>
    findAllByGroupIdAndMemberIdAndWatchTypeAndWatchValueAndEnabledTrue(
            Long groupId, Long memberId, String watchType, String watchValue);

    /**
     * 按成员和完整订阅条件读取已启用规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRule>
    findAllByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValueAndEnabledTrue(
            Long groupId, Long memberId, String watchType, String watchKey, String watchValue);

    /**
     * 查询指定主键对应的首条记录。
     *
     * @param id 目标记录的本地主键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeGroupWatchRule> findFirstById(Long id);

    /**
     * 读取指定群组成员的全部非受管订阅规则投影。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeGroupWatchRuleDTO> findAllProjectedByGroupIdAndMemberId(Long groupId, Long memberId);
}
