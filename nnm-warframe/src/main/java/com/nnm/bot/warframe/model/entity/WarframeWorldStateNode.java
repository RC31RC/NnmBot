package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_worldstate_node")
public class WarframeWorldStateNode extends WarframeWorldStateRecord {
    @Column(name = "display_name", nullable = false, length = 500)
    private String displayName = "";
    @Column(name = "faction_reference", nullable = false, length = 200)
    private String factionReference = "";
    @Column(name = "mission_type_reference", nullable = false, length = 200)
    private String missionTypeReference = "";
}
