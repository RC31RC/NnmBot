package com.nnm.bot.admin.domain;

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

@Getter
@Setter
@Entity
@Table(name = "sys_menu")
public class SysMenu {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 父菜单主键，顶级菜单为空。 */
    @Column(name = "parent_id")
    private Long parentId;

    /** 前端路由名称。 */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** 前端路由路径。 */
    @Column(nullable = false, length = 255)
    private String path;

    /** 前端页面组件路径。 */
    @Column(length = 255)
    private String component;

    /** 菜单显示标题。 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 菜单图标编码。 */
    @Column(length = 100)
    private String icon;

    /** 同级菜单排序值。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 是否缓存页面组件。 */
    @Column(name = "keep_alive", nullable = false)
    private boolean keepAlive;

    /** 是否固定在标签页。 */
    @Column(name = "fixed_tab", nullable = false)
    private boolean fixedTab;

    /** 是否隐藏标签页。 */
    @Column(name = "hide_tab", nullable = false)
    private boolean hideTab;

    /** 是否以全屏页面打开。 */
    @Column(name = "full_page", nullable = false)
    private boolean fullPage;

    /** 是否以 iframe 页面打开。 */
    @Column(name = "iframe", nullable = false)
    private boolean iframe;

    /** 外部链接地址。 */
    @Column(length = 500)
    private String link;

    /** 页面激活时使用的高亮路径。 */
    @Column(name = "active_path", length = 255)
    private String activePath;

    /** 菜单文字徽标。 */
    @Column(name = "text_badge", length = 50)
    private String textBadge;

    /** 是否在导航中显示。 */
    @Column(nullable = false)
    private boolean visible = true;

    /** 是否允许访问该菜单。 */
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
