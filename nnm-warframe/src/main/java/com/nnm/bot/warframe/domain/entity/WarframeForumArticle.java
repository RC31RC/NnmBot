package com.nnm.bot.warframe.domain.entity;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Warframe 论坛文章。
 *
 * <p>文章正文明确映射为 PostgreSQL TEXT，不使用 {@code @Lob}，避免 PostgreSQL
 * 将管理端内容存储为大型对象并要求额外的事务上下文。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "wf_forum_article")
public class WarframeForumArticle {

    @Id
    private Long id;

    @Column(nullable = false, length = 160, unique = true)
    private String slug;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(length = 1000)
    private String summary;

    @Column(length = 1000)
    private String tags;

    @Column(length = 120)
    private String category;

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "page_url", nullable = false, length = 1000)
    private String pageUrl;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

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
        slug = StrUtil.trim(slug);
        title = StrUtil.trim(title);
        summary = trimToNull(summary);
        tags = trimToNull(tags);
        category = trimToNull(category);
        coverUrl = trimToNull(coverUrl);
        contentHtml = StrUtil.trim(contentHtml);
        contentMarkdown = trimToNull(contentMarkdown);
        pageUrl = StrUtil.trim(pageUrl);
    }

    private static String trimToNull(String value) {
        String trimmed = StrUtil.trim(value);
        return StrUtil.isBlank(trimmed) ? null : trimmed;
    }
}
