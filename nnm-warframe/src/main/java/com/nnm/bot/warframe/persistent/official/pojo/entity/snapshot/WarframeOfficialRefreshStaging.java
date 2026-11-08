package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/** 上传冻结对象前持久化的短暂恢复账本；不参与业务数据查询。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_dataset_staging")
public class WarframeOfficialRefreshStaging {
    @Id
    @Column(length = 36)
    private String id;
    @Column(name = "manifest_json", nullable = false, columnDefinition = "TEXT")
    private String manifestJson;
    @Column(name = "product_path", length = 260)
    private String productPath;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
