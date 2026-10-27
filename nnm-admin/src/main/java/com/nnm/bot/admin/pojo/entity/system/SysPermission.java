package com.nnm.bot.admin.pojo.entity.system;

import com.nnm.bot.admin.constant.PermissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sys_permission")
public class SysPermission {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 稳定权限码。 */
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    /** 权限显示名称。 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 权限类型，例如菜单权限或按钮权限。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PermissionType type;

    /** 所属菜单主键。 */
    @Column(name = "menu_id")
    private Long menuId;

    /** 是否启用该权限。 */
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
