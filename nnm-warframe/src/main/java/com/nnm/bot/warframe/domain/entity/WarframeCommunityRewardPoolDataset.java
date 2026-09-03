package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** reward_pool.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_reward_pool")
public class WarframeCommunityRewardPoolDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "unique_name", length = 500)
    private String uniqueName;

    @Column(name = "reward_count")
    private Integer rewardCount;

    @Column(name = "rewards_json", columnDefinition = "TEXT")
    private String rewardsJson;
}
