package com.nnm.bot.admin.pojo.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/** 每名管理员一条独立通知；删除/到期不影响其他管理员的消息。 */
@Getter
@Setter
@Entity
@Table(name = "sys_admin_notice")
public class AdminNotice {
    @Id
    @Column(length = 36)
    private String id;
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;
    @Column(name = "source_key", length = 128, unique = true)
    private String sourceKey;
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "required_permission", length = 100)
    private String requiredPermission;
    @Column(name = "publisher_id")
    private Long publisherId;
    @Column(name = "task_id", length = 36)
    private String taskId;
    @Column(name = "task_type", length = 400)
    private String taskType;
    @Column(name = "task_status", length = 16)
    private String taskStatus;
    @Column(name = "preview_id", length = 36)
    private String previewId;
    @Column(name = "preview_dataset", length = 100)
    private String previewDataset;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
