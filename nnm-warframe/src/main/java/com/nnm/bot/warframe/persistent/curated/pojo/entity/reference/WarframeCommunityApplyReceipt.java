package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** 按预览标识记录一次已采纳结果，供重复提交校验选择范围并复用处理结果。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_apply_receipt")
public class WarframeCommunityApplyReceipt {
    @Id
    @Column(length = 36)
    private String previewId;
    @Column(nullable = false, length = 50)
    private String dataset;
    /** 本次采纳记录集合的摘要，区分同一预览的不同选择。 */
    @Column(nullable = false, length = 64)
    private String selectionHash;
    @Column(nullable = false)
    private int appliedCount;
    @Column(nullable = false)
    private Instant appliedAt;
}
