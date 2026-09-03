package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** state_translation.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_state_translation")
public class WarframeCommunityStateTranslationDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "unique_name", length = 500)
    private String uniqueName;

    @Column(name = "display_name", length = 500)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;
}
