package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_riven_unrollable")
/**
 * Public Export Plus 提供的不可展开紫卡属性数据。
 */
public class WarframeCommunityRivenUnrollable extends WarframeCommunityDatasetRecord {
    @Column(name = "attribute_code", nullable = false, length = 300)
    private String attributeCode;
}
