package com.nnm.bot.admin.pojo.entity.system;

import com.nnm.bot.admin.constant.DataScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class SysRole {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 系统角色编码。 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 角色显示名称。 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 角色说明。 */
    @Column(length = 255)
    private String description;

    /** 是否启用该角色。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 角色权限等级，用于限制高等级对象被低等级操作者管理。 */
    @Column(name = "authority_level", nullable = false)
    private int authorityLevel;

    /** 角色可见的数据范围。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_scope", nullable = false, length = 20)
    private DataScope dataScope = DataScope.SELF;

    /** 角色拥有的按钮和接口权限。 */
    @ManyToMany
    @JoinTable(
            name = "sys_role_permission",
            joinColumns =
                    @JoinColumn(name = "role_id"),
            inverseJoinColumns =
                    @JoinColumn(name = "permission_id")
    )
    private Set<SysPermission> permissions = new LinkedHashSet<>();

    /** 角色可见的菜单集合。 */
    @ManyToMany
    @JoinTable(
            name = "sys_role_menu",
            joinColumns =
                    @JoinColumn(name = "role_id"),
            inverseJoinColumns =
                    @JoinColumn(name = "menu_id")
    )
    private Set<SysMenu> menus = new LinkedHashSet<>();

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
