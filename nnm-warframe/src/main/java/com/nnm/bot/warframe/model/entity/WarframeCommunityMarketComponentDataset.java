package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Locally maintained WM component suffix rules. */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_market_component")
public class WarframeCommunityMarketComponentDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "input_alias", nullable = false, length = 200)
    private String inputAlias;

    @Column(name = "market_suffix", nullable = false, length = 200)
    private String marketSuffix;
}
