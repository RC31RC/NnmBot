package com.nnm.bot.core.group.domain;

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

/** 逻辑群组，作为跨渠道的隔离主体。 */
@Getter
@Setter
@Entity
@Table(name = "bot_group")
public class BotGroup {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 逻辑群组编码。 */
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    /** 群组显示名称。 */
    @Column(nullable = false, length = 160)
    private String name;

    /** 群组用途说明。 */
    @Column(length = 255)
    private String description;

    /** 是否启用该逻辑群组。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
