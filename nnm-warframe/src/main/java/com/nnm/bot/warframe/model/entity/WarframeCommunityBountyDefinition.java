package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** WorldState jobType 引用的静态定义，主记录与全部阶段作为一个采纳单位。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_bounty_definition")
public class WarframeCommunityBountyDefinition extends WarframeCommunityDatasetRecord {
    @Column(name = "name_key", length = 500)
    private String nameKey;
    @Column(name = "description_key", length = 500)
    private String descriptionKey;
    @Column(name = "icon", length = 500)
    private String icon;

    @ElementCollection
    @CollectionTable(name = "wf_community_bounty_stage",
            joinColumns = @JoinColumn(name = "definition_id"))
    @OrderColumn(name = "entry_order")
    private List<WarframeBountyStageOption> stages = new ArrayList<>();
}
