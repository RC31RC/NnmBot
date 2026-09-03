package com.nnm.bot.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 机器人能力角色，与后台用户的系统 RBAC 角色完全分离。 */
@Getter
@Setter
@Entity
@Table(name = "bot_robot_role")
public class BotRobotRole {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机器人能力角色编码。 */
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    /** 机器人能力角色名称。 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 能力角色说明。 */
    @Column(name = "description", length = 255)
    private String description;

    /** 是否允许实例使用该能力角色。 */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /** 创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @jakarta.persistence.PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
