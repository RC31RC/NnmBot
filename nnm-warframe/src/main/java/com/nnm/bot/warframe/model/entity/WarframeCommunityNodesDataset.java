package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** nodes.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_nodes")
public class WarframeCommunityNodesDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "unique_name", length = 500)
    private String uniqueName;

    @Column(name = "display_name", length = 500)
    private String displayName;

    @Column(name = "system_index")
    private Integer systemIndex;

    @Column(name = "system_name", length = 200)
    private String systemName;

    @Column(name = "node_type")
    private Integer nodeType;

    @Column(name = "mastery_req")
    private Integer masteryReq;

    @Column(name = "mission_index")
    private Integer missionIndex;

    @Column(name = "faction_index")
    private Integer factionIndex;

    @Column(name = "min_enemy_level")
    private Integer minEnemyLevel;

    @Column(name = "max_enemy_level")
    private Integer maxEnemyLevel;
}
