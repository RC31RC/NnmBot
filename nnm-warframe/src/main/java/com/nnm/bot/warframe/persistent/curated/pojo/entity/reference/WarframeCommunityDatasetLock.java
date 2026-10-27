package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_dataset_lock")
public class WarframeCommunityDatasetLock {
    @Id
    @Column(length = 50)
    private String dataset;
}
