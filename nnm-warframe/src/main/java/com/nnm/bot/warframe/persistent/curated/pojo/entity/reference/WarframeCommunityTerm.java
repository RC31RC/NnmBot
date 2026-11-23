package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 官方静态词典缺失时使用的人工术语。分组限定赏金阶段、友军或活动条件等适用范围。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_term")
public class WarframeCommunityTerm extends WarframeCommunityRow {
    /** 术语的适用业务，例如赏金的地点短码和完整友军路径使用不同分组。 */
    @Column(name = "term_group", nullable = false, columnDefinition = "TEXT")
    private String termGroup;

    /** 来源原词，也可为只在本分组有意义的短码或路径。 */
    @Column(name = "english_text", nullable = false, columnDefinition = "TEXT")
    private String englishText;

    @Column(name = "chinese_text", nullable = false, columnDefinition = "TEXT")
    private String chineseText;

}
