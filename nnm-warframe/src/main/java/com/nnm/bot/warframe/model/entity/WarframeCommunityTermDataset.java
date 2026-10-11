package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 参考项目本地维护的通用中文术语、Riven 词典和活动术语。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_term")
public class WarframeCommunityTermDataset extends WarframeCommunityDatasetRecord {

    @Column(name = "term_group", nullable = false, length = 64)
    private String termGroup;

    @Column(name = "english_text", length = 500)
    private String englishText;

    @Column(name = "chinese_text", length = 500)
    private String chineseText;
}
