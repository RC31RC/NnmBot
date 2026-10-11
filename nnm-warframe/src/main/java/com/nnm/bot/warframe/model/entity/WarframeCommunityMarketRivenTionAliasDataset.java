package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** market_riven_tion_alias.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_market_riven_tion_alias")
public class WarframeCommunityMarketRivenTionAliasDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "attribute_code", length = 200)
    private String attributeCode;

    @Column(name = "alias_name", length = 200)
    private String aliasName;
}
