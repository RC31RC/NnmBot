package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_lock")
public class WarframeCommunityDatasetLock {
    /** 数据库可见内容版本；采纳和人工维护均在原事务内递增。 */
    @Column(name = "data_revision", nullable = false)
    private long dataRevision;

    @Id
    @Column(length = 50)
    private String dataset;
}
