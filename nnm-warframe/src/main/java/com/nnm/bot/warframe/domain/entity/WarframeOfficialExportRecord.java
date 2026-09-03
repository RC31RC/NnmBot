package com.nnm.bot.warframe.domain.entity;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 官方 PublicExport 的一条完整记录。 */
@Getter
@Setter
@Entity
@Table(
        name = "wf_official_export_record",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_wf_official_export_identity",
                columnNames = {"export_type", "unique_name"}))
public class WarframeOfficialExportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_name", nullable = false, length = 500)
    private String uniqueName;

    @Column(name = "export_file", nullable = false, length = 260)
    private String exportFile;

    @Column(name = "export_type", nullable = false, length = 120)
    private String exportType;

    @Column(name = "record_type", length = 160)
    private String recordType;

    @Column(name = "official_name", length = 500)
    private String officialName;

    @Column(name = "localized_name", length = 500)
    private String localizedName;

    @Column(name = "parent_name", length = 500)
    private String parentName;

    @Column(name = "product_category", length = 160)
    private String productCategory;

    @Column(name = "exclude_from_codex")
    private Boolean excludeFromCodex;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    @Column(name = "wiki_slug", length = 300)
    private String wikiSlug;

    @Column(name = "market_slug", length = 300)
    private String marketSlug;

    @Column(name = "raw_json", nullable = false, columnDefinition = "TEXT")
    private String rawJson;

    @Column(name = "localized_raw_json", columnDefinition = "TEXT")
    private String localizedRawJson;

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
        uniqueName = StrUtil.trim(uniqueName);
        exportFile = StrUtil.trim(exportFile);
        officialName = trimToNull(officialName);
        localizedName = trimToNull(localizedName);
        exportType = trimToNull(exportType);
        recordType = trimToNull(recordType);
        parentName = trimToNull(parentName);
        productCategory = trimToNull(productCategory);
        description = trimToNull(description);
        longDescription = trimToNull(longDescription);
        wikiSlug = trimToNull(wikiSlug);
        marketSlug = trimToNull(marketSlug);
    }

    private static String trimToNull(String value) {
        String trimmed = StrUtil.trim(value);
        return StrUtil.isBlank(trimmed) ? null : trimmed;
    }
}
