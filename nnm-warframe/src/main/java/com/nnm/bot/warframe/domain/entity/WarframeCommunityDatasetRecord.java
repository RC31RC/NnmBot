package com.nnm.bot.warframe.domain.entity;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;

import java.time.LocalDateTime;

/**
 * 社区 JSON 数据集的公共记录。
 *
 * <p>currentJson 是当前运行版本，upstreamJson 是最近一次上游版本，
 * originalJson 用于追溯首次导入内容；manualFieldsJson 只记录人工改过的顶层字段。</p>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class WarframeCommunityDatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 500)
    private String recordKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_source", nullable = false, length = 32)
    private WarframeCommunityRecordSource recordSource = WarframeCommunityRecordSource.COMMUNITY;

    @Column(name = "current_json", nullable = false, columnDefinition = "TEXT")
    private String currentJson;

    @Column(name = "upstream_json", nullable = false, columnDefinition = "TEXT")
    private String upstreamJson;

    @Column(name = "original_json", nullable = false, columnDefinition = "TEXT")
    private String originalJson;

    @Column(name = "manual_fields_json", nullable = false, columnDefinition = "TEXT")
    private String manualFieldsJson = "{}";

    @Column(name = "upstream_hash", nullable = false, length = 64)
    private String upstreamHash;

    @Column(name = "source_hash", nullable = false, length = 64)
    private String sourceHash;

    @Column(name = "manual_modified", nullable = false)
    private boolean manualModified;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        normalize();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    private void normalize() {
        recordKey = StrUtil.trim(recordKey);
        currentJson = StrUtil.blankToDefault(currentJson, "{}");
        upstreamJson = StrUtil.blankToDefault(upstreamJson, "{}");
        originalJson = StrUtil.blankToDefault(originalJson, "{}");
        manualFieldsJson = StrUtil.blankToDefault(manualFieldsJson, "{}");
        upstreamHash = StrUtil.blankToDefault(upstreamHash, "");
        sourceHash = StrUtil.blankToDefault(sourceHash, "");
    }
}
