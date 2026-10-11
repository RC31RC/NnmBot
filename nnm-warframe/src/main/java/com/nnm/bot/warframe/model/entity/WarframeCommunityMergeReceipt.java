package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** 与采纳操作一起提交；Redis 完全不可用时仍能阻止批次重放。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_merge_receipt")
public class WarframeCommunityMergeReceipt {
    @Id
    @Column(name = "preview_id", length = 36)
    private String previewId;

    @Column(name = "dataset", nullable = false, length = 100)
    private String dataset;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;
}
