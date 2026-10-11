package com.nnm.bot.warframe.model.entity;

import com.nnm.bot.warframe.constant.WarframeWorldStateSortieRecordType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_worldstate_sortie")
public class WarframeWorldStateSortie extends WarframeWorldStateRecord {
    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 16)
    private WarframeWorldStateSortieRecordType recordType;

    @Column(name = "display_name", nullable = false, length = 500)
    private String displayName = "";

    @Column(nullable = false, length = 2000)
    private String description = "";

    @Column(name = "faction_reference", nullable = false, length = 200)
    private String factionReference = "";
}
