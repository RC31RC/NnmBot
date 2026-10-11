package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 完整翻译键的单语言投影。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_translation")
public class WarframeCommunityTranslationDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(length = 16)
    private String locale;

    @Column(name = "display_name", columnDefinition = "TEXT")
    private String displayName;

    @Column(name = "prime_item")
    private Boolean primeItem;

    @Column(name = "set_item")
    private Boolean setItem;
}
