package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class WarframeWorldStateRecord extends WarframeCommunityDatasetRecord {
    @Column(name = "locale", nullable = false, length = 16)
    private String locale;
}
