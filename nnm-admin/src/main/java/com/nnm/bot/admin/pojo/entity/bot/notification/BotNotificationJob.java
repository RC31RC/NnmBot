package com.nnm.bot.admin.pojo.entity.bot.notification;

import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** 定时任务落库，内容与资源是提交时的快照；连接及发送资格在执行时重新判断。 */
@Getter
@Setter
@Entity
@Table(name = "bot_notification_job")
public class BotNotificationJob {
    @Id
    private UUID id;
    @Column(name = "owner_id", nullable = false)
    private long ownerId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private BotGroupNotificationDTO request;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private NotificationResourceManifest resources;
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private JobStatus status;
    @Column(name = "task_id")
    private UUID taskId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private BotGroupNotificationReportDTO report;
    @Column(length = 1000)
    private String error;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
