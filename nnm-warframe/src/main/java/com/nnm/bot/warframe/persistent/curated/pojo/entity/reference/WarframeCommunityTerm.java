package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_term")
public class WarframeCommunityTerm extends WarframeCommunityRow {
    @Column(name = "term_group", nullable = false, columnDefinition = "TEXT")
    private String termGroup;

    @Column(name = "english_text", nullable = false, columnDefinition = "TEXT")
    private String englishText;

    @Column(name = "chinese_text", nullable = false, columnDefinition = "TEXT")
    private String chineseText;

}
