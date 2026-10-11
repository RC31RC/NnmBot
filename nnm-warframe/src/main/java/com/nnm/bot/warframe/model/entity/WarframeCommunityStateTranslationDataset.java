package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 世界状态名称及说明词库，供引用解析器和赏金展示读取。
 * datasource 的 uniqueName/name/description 分别映射本实体三列。
 * public-export-plus 补充记录通过 ExportBounties 的词条键关联 dict.zh（简体）和 dict.en，
 * name/description 保存中文优先结果，英文及来源保留在 currentJson 中。
 */
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
