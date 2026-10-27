package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_alias")
public class WarframeCommunityAlias extends WarframeCommunityRow {
    @Column(name = "alias_name", nullable = false, columnDefinition = "TEXT")
    private String aliasName;

    @Column(name = "target_name", nullable = false, columnDefinition = "TEXT")
    private String targetName;

}
