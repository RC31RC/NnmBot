package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** riven_analyse_trend.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_riven_analyse_trend")
public class WarframeCommunityRivenAnalyseTrendDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "display_name", length = 500)
    private String displayName;

    @Column(length = 100)
    private String prefix;

    @Column(length = 100)
    private String suffix;

    private Double archwing;

    @Column(name = "melee_value")
    private Double meleeValue;

    private Double pistol;

    private Double rifle;

    private Double shotgun;
}
