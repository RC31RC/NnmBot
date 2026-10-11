package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "wf_community_relic_reward")
@AttributeOverride(name = "recordKey", column = @Column(name = "record_key", nullable = false, length = 700))
/**
 * 社区遗物奖励明细。
 *
 * <p>relicRecordKey 和 rewardOrder 是导入时的外围关联字段，原始奖励 JSON
 * 仍由统一记录基类保留。</p>
 */
public class WarframeCommunityRelicReward extends WarframeCommunityDatasetRecord {
    @Column(name = "relic_record_key", nullable = false, length = 500)
    private String relicRecordKey;
    @Column(name = "reward_name", length = 300)
    private String rewardName;
    @Column(name = "unique_name", length = 500)
    private String uniqueName;
    @Column(name = "market_id", length = 100)
    private String marketId;
    @Column(name = "url_name", length = 200)
    private String urlName;
    @Column(length = 32)
    private String rarity;
    private BigDecimal chance;
    @Column(name = "reward_order", nullable = false)
    private Integer rewardOrder;
}
