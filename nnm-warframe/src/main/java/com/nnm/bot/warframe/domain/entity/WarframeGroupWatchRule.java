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

/**
 * 群组成员长期维护的 Warframe 蹲任务规则。
 *
 * <p>groupId 和 memberId 是跨模块的逻辑关联，不声明 JPA 关联，避免 Warframe
 * 模块反向依赖管理端数据模型。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "wf_group_watch_rule")
public class WarframeGroupWatchRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "watch_type", nullable = false, length = 80)
    private String watchType;

    @Column(name = "watch_key", nullable = false, length = 160)
    private String watchKey;

    @Column(name = "watch_value", nullable = false, length = 500)
    private String watchValue;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "notify_enabled", nullable = false)
    private boolean notifyEnabled = true;

    @Column(name = "last_match_key", length = 500)
    private String lastMatchKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        normalize();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    private void normalize() {
        watchType = StrUtil.trim(watchType);
        watchKey = StrUtil.trim(watchKey);
        watchValue = StrUtil.trim(watchValue);
        lastMatchKey = StrUtil.trim(lastMatchKey);
    }
}
