package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport 星图节点结构化字段。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_region")
public class WarframeOfficialRegion {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    @Column(name = "system_name", length = 160)
    private String systemName;
    @Column(name = "system_index")
    private Integer systemIndex;
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
