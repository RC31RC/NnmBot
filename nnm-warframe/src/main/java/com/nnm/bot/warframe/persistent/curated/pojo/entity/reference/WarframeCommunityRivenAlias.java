package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_riven_alias")
public class WarframeCommunityRivenAlias extends WarframeCommunityRow {
    @Column(name = "attribute_code", nullable = false, columnDefinition = "TEXT")
    private String attributeCode;

    @Column(name = "alias_name", nullable = false, columnDefinition = "TEXT")
    private String aliasName;

}
