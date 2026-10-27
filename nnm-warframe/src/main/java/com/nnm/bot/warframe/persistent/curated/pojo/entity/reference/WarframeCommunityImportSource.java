package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_import_source")
public class WarframeCommunityImportSource {
    @Id
    @Column(length = 50)
    private String dataset;
    @Column(name = "raw_url", columnDefinition = "TEXT", nullable = false)
    private String rawUrl;
}
