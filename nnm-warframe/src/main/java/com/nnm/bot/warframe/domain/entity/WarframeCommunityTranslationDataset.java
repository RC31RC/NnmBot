package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** translation.json 数据集记录。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_translation")
public class WarframeCommunityTranslationDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "chinese_name", length = 500)
    private String chineseName;

    @Column(name = "english_name", length = 500)
    private String englishName;

    @Column(name = "prime_item")
    private Boolean primeItem;

    @Column(name = "set_item")
    private Boolean setItem;
}
