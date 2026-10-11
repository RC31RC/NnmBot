package com.nnm.bot.warframe.model.entity;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * <p>currentJson 是当前生效的数据，upstreamJson 是本次同步保存的上游数据，
 * originalJson 保留首次导入内容；历史人工字段列仅保留存储兼容，不再参与社区刷新合并。</p>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class WarframeCommunityDatasetRecord {

    @Id
    private Long id;

    @Column(name = "record_key", nullable = false, length = 500)
    private String recordKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_source", nullable = false, length = 32)
    private WarframeCommunityRecordSource recordSource = WarframeCommunityRecordSource.COMMUNITY;

    /**
     * 具体上游来源代号，例如 kingprimes:warframe/translation.json。
     * 与 recordSource 的 COMMUNITY/SELF_MAINTAINED 分类不同，这个字段用于审计和刷新隔离。
     */
    @Column(name = "source_code", length = 200)
    private String sourceCode;

    @Column(name = "last_imported_at")
    private LocalDateTime lastImportedAt;

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
        sourceCode = StrUtil.trim(sourceCode);
        currentJson = StrUtil.blankToDefault(currentJson, "{}");
        upstreamJson = StrUtil.blankToDefault(upstreamJson, "{}");
        originalJson = StrUtil.blankToDefault(originalJson, "{}");
        manualFieldsJson = StrUtil.blankToDefault(manualFieldsJson, "{}");
        upstreamHash = StrUtil.blankToDefault(upstreamHash, "");
        sourceHash = StrUtil.blankToDefault(sourceHash, "");
    }
}
