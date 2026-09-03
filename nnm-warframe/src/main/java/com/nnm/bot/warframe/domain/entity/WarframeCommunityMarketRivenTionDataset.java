package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** market_riven_tion.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_market_riven_tion")
public class WarframeCommunityMarketRivenTionDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(length = 500)
    private String effect;

    @Column(name = "attribute_group", length = 100)
    private String attributeGroup;

    @Column(name = "negative_only")
    private Boolean negativeOnly;

    @Column(name = "positive_is_negative")
    private Boolean positiveIsNegative;

    @Column(length = 100)
    private String prefix;

    @Column(name = "search_only")
    private Boolean searchOnly;

    @Column(length = 100)
    private String suffix;

    @Column(length = 100)
    private String units;

    @Column(name = "url_name", length = 200)
    private String urlName;

    @Column(name = "exclusive_to_json", columnDefinition = "TEXT")
    private String exclusiveToJson;
}
