package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** riven_trend.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_riven_trend")
public class WarframeCommunityRivenTrendDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "trend_time", length = 32)
    private String trendTime;

    @Column(name = "new_dot", length = 32)
    private String newDot;

    @Column(name = "new_num")
    private Double newNum;

    @Column(name = "old_dot", length = 32)
    private String oldDot;

    @Column(name = "old_num")
    private Double oldNum;

    @Column(name = "trend_name", length = 500)
    private String trendName;

    @Column(name = "weapon_type", length = 100)
    private String weaponType;
}
