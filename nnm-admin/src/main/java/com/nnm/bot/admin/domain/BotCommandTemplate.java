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
@Table(name = "bot_command_template")
public class BotCommandTemplate {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 模板稳定编码，供命令配置引用。 */
    @Column(name = "template_code", nullable = false, unique = true, length = 80)
    private String templateCode;

    /** 模板显示名称。 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 所属命令主键，使用应用层逻辑关联，不建立数据库外键。 */
    @Column(name = "command_id")
    private Long commandId;

    /** 同一命令下模板的展示和默认渲染顺序，数值越小越靠前。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 使用 Thymeleaf 表达式的完整 HTML 内容。 */
    @Column(name = "html_content", nullable = false, columnDefinition = "TEXT")
    private String htmlContent;

    /** 是否允许命令引用该模板。 */
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
