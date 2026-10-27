package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_arbitration_essence")
public class WarframeCommunityArbitrationEssence extends WarframeCommunityRow {
    @Column(name = "node_name", nullable = false, columnDefinition = "TEXT")
    private String nodeName;

    @Column(name = "essence_amount", nullable = false)
    private Integer essenceAmount;

}
