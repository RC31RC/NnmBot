package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 完整奖励池来源记录；标准化奖励项保留概率小数，原始字段保存在 JSON 快照中。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_reward_pool")
public class WarframeCommunityRewardPoolDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "pool_key", length = 500)
    private String poolKey;

    @Column(name = "reward_items_json", nullable = false, columnDefinition = "TEXT")
    private String rewardItemsJson;
}
