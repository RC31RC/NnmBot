package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** alias.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_alias")
public class WarframeCommunityAliasDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "alias_name", length = 500)
    private String aliasName;

    @Column(name = "target_name", length = 500)
    private String targetName;

    @Column(name = "normalized_alias", length = 500)
    private String normalizedAlias;

    @Column(name = "manual_item_id")
    private Long manualItemId;

    @Column(name = "remark", length = 1000)
    private String remark;
}
