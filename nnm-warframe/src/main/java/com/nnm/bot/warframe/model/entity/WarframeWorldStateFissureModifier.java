package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_worldstate_fissure_modifier")
public class WarframeWorldStateFissureModifier extends WarframeWorldStateRecord {
    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName = "";
    @Column(name = "tier_number", nullable = false)
    private int tierNumber;
}
