package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 维护外部业务字段在指定解析场景中的目标身份或显示补充。 */
@Entity
@Table(name = "wf_community_reference_mapping")
@Getter
@Setter
public class WarframeCommunityReferenceMapping extends WarframeCommunityRow {
    @Column(name = "mapping_scope", nullable = false, length = 50)
    private String mappingScope;

    @Column(name = "source_key", nullable = false, columnDefinition = "TEXT")
    private String sourceKey;

    @Column(name = "target_key", nullable = false, columnDefinition = "TEXT")
    private String targetKey;
}
