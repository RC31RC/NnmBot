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

/** 管理员维护的人工别名，manualItemId 为逻辑关联。 */
@Getter
@Setter
@Entity
@Table(name = "wf_manual_alias")
public class WarframeManualAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manual_item_id", nullable = false)
    private Long manualItemId;

    @Column(name = "alias_name", nullable = false, length = 300)
    private String aliasName;

    @Column(name = "normalized_alias", nullable = false, length = 300)
    private String normalizedAlias;

    @Column(name = "remark", length = 1000)
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
        aliasName = StrUtil.trim(aliasName);
        normalizedAlias = StrUtil.trim(normalizedAlias);
        remark = StrUtil.isBlank(remark) ? null : StrUtil.trim(remark);
    }
}
