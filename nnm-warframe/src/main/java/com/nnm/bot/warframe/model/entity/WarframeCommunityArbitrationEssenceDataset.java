package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 仲裁节点对应的精华数量规则，不是动态仲裁状态。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_arbitration_essence")
public class WarframeCommunityArbitrationEssenceDataset
        extends WarframeCommunityDatasetRecord {

    @Column(name = "node_name", nullable = false, length = 200)
    private String nodeName;

    @Column(name = "essence_amount", nullable = false)
    private Integer essenceAmount;
}
