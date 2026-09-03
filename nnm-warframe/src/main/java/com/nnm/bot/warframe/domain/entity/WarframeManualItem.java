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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 管理员维护的人工物品数据。 */
@Getter
@Setter
@Entity
@Table(name = "wf_manual_item")
public class WarframeManualItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_key", nullable = false, length = 500, unique = true)
    private String sourceKey;

    @Column(name = "official_name", nullable = false, length = 500)
    private String officialName;

    @Column(name = "localized_name", nullable = false, length = 500)
    private String localizedName;

    @Column(name = "normalized_name", nullable = false, length = 500)
    private String normalizedName;

    @Column(name = "wiki_slug", length = 300)
    private String wikiSlug;

    @Column(name = "market_slug", length = 300)
    private String marketSlug;

    @Column(name = "remark", length = 1000)
    private String remark;

    @Column(name = "raw_json", columnDefinition = "TEXT")
    private String rawJson;

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
        sourceKey = StrUtil.trim(sourceKey);
        officialName = StrUtil.trim(officialName);
        localizedName = StrUtil.trim(localizedName);
        normalizedName = StrUtil.trim(normalizedName);
        wikiSlug = trimToNull(wikiSlug);
        marketSlug = trimToNull(marketSlug);
        remark = trimToNull(remark);
        rawJson = trimToNull(rawJson);
    }

    private static String trimToNull(String value) {
        String trimmed = StrUtil.trim(value);
        return StrUtil.isBlank(trimmed) ? null : trimmed;
    }
}
