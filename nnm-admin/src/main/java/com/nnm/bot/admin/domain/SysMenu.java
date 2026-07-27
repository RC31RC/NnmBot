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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(length = 255)
    private String component;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "keep_alive", nullable = false)
    private boolean keepAlive;

    @Column(name = "fixed_tab", nullable = false)
    private boolean fixedTab;

    @Column(name = "hide_tab", nullable = false)
    private boolean hideTab;

    @Column(name = "full_page", nullable = false)
    private boolean fullPage;

    @Column(name = "iframe", nullable = false)
    private boolean iframe;

    @Column(length = 500)
    private String link;

    @Column(name = "active_path", length = 255)
    private String activePath;

    @Column(name = "text_badge", length = 50)
    private String textBadge;

    @Column(nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

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
