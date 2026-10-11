package com.nnm.bot.warframe.model.entity;

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

/** 与人工物品维护记录对应的人工翻译，不受社区数据刷新覆盖。 */
@Getter
@Setter
@Entity
@Table(name = "wf_manual_translation", uniqueConstraints = @UniqueConstraint(
        name = "uq_wf_manual_translation_item_locale",
        columnNames = {"item_id", "locale"}))
public class WarframeManualTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false, length = 20)
    private String locale;

    @Column(name = "translated_name", nullable = false, length = 500)
    private String translatedName;

    @Column(columnDefinition = "TEXT")
    private String remark;

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
        locale = StrUtil.trim(locale);
        translatedName = StrUtil.trim(translatedName);
        remark = StrUtil.trim(remark);
    }
}
