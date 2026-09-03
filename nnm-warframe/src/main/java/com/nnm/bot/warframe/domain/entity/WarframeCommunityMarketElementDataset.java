package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** market_element.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_market_element")
public class WarframeCommunityMarketElementDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "element_code", length = 200)
    private String elementCode;

    @Column(name = "display_name", length = 200)
    private String displayName;
}
