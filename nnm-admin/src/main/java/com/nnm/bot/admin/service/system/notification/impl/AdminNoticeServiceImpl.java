package com.nnm.bot.admin.service.system.notification.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.admin.config.AdminNoticeProperties;
import com.nnm.bot.admin.pojo.dto.system.notification.AdminNoticePublishDTO;
import com.nnm.bot.admin.pojo.entity.system.AdminNotice;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.vo.system.notification.AdminNoticeVO;
import com.nnm.bot.admin.repository.system.AdminNoticeRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.system.notification.AdminNoticeService;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;
import com.nnm.bot.common.task.AdminTaskEvent;
import com.nnm.bot.common.task.AdminTaskFinishedEvent;
import com.nnm.bot.common.validation.BusinessAssert;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 任务结束时生成每位有权管理员的消息；预览消息以预览到期时间为上限。 */
@Slf4j
@Service
public class AdminNoticeServiceImpl implements AdminNoticeService {
    @Resource
    private AdminNoticeRepository notices;
    @Resource
    private SysUserRepository users;
    @Resource
    private AdminNoticeProperties properties;
    @Resource
    private ObjectMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<AdminNoticeVO> list(long userId) {
        return notices.findTop100ByRecipientIdAndExpiresAtAfterOrderByCreatedAtDesc(userId, Instant.now()).stream()
            .filter(notice -> notice.getRequiredPermission() == null
                || StpUtil.hasPermission(notice.getRequiredPermission()))
            .map(this::view).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AdminNoticeVO> history(long userId, int current, int size) {
        BusinessAssert.isTrue(current > 0 && size > 0 && size <= 100, CommonErrorCode.PARAM_INVALID);
        List<AdminNoticeVO> visible =
            notices.findByRecipientIdAndExpiresAtAfterOrderByCreatedAtDesc(userId, Instant.now()).stream()
                .filter(notice -> notice.getRequiredPermission() == null
                    || StpUtil.hasPermission(notice.getRequiredPermission()))
                .map(this::view).toList();
        long start = ((long)current - 1) * size;
        if (start >= visible.size())
            return PageResult.of(List.of(), current, size, visible.size());
        int from = (int)start;
        return PageResult.of(visible.subList(from, Math.min(from + size, visible.size())), current, size,
            visible.size());
    }

    @Override
    @Transactional
    public void publish(long publisherId, AdminNoticePublishDTO request) {
        Instant now = Instant.now();
        BusinessAssert.isTrue(
            request.expiresAt().isAfter(now)
                && !request.expiresAt().isAfter(now.plus(properties.getMaxManualRetention())),
            CommonErrorCode.PARAM_INVALID, "通知过期时间须在将来且不得超过最长有效期");
        String title = request.title().trim();
        String content = request.content().trim();
        BusinessAssert.isTrue(!title.isEmpty() && !content.isEmpty(), CommonErrorCode.PARAM_INVALID);
        List<AdminNotice> batch = new ArrayList<>();
        for (SysUser user : users.findAll()) {
            if (admin(user)) {
                AdminNotice notice = base(user.getId(), title, content, now, request.expiresAt());
                notice.setPublisherId(publisherId);
                batch.add(notice);
            }
        }
        notices.saveAll(batch);
    }

    /** 由任务线程广播终态；不读取或传递 Web/Sa-Token 的线程上下文。 */
    @EventListener
    @Transactional
    public void taskFinished(AdminTaskFinishedEvent finished) {
        AdminTaskEvent event = finished.event();
        if (event.status() != AdminTaskStatus.COMPLETED && event.status() != AdminTaskStatus.FAILED
            && event.status() != AdminTaskStatus.CANCELLED)
            return;
        JsonNode result = event.result() == null ? mapper.nullNode() : mapper.valueToTree(event.result());
        String previewId = text(result, "id");
        String dataset = text(result, "dataset");
        String expires = text(result, "expiresAt");
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getTaskHistoryRetention());
        if (previewId != null && expires != null) {
            try {
                expiresAt = Instant.parse(expires);
            } catch (RuntimeException invalid) {
                log.warn("预览时间异常, taskId={}", event.taskId(), invalid);
            }
        }
        if (!expiresAt.isAfter(now))
            return;
        List<AdminNotice> batch = new ArrayList<>();
        String permission = finished.requiredPermission();
        for (SysUser user : users.findAll()) {
            if (!admin(user) || !allowed(user, permission))
                continue;
            String sourceKey = "task:" + event.taskId() + ":" + user.getId();
            if (notices.existsBySourceKey(sourceKey))
                continue;
            AdminNotice notice = base(user.getId(), event.taskType(), event.message(), now, expiresAt);
            notice.setSourceKey(sourceKey);
            notice.setRequiredPermission(permission);
            notice.setTaskId(event.taskId().toString());
            notice.setTaskType(event.taskType());
            notice.setTaskStatus(event.status().name());
            notice.setPreviewId(previewId);
            notice.setPreviewDataset(dataset);
            batch.add(notice);
        }
        notices.saveAll(batch);
    }

    @Override
    @Transactional
    public void dismiss(long userId, String id) {
        notices.deleteByIdAndRecipientId(id, userId);
    }

    @Override
    @Transactional
    public void dismissAll(long userId) {
        notices.deleteByRecipientId(userId);
    }

    @Override
    @Transactional
    public void cleanup(Instant now) {
        notices.deleteByExpiresAtBefore(now);
    }

    private AdminNoticeVO view(AdminNotice value) {
        return new AdminNoticeVO(value.getId(), value.getTitle(), value.getContent(), value.getTaskId(),
            value.getTaskType(), value.getTaskStatus(), value.getPreviewId(), value.getPreviewDataset(),
            value.getCreatedAt(), value.getExpiresAt());
    }

    private static AdminNotice base(long recipientId, String title, String content, Instant now, Instant expiresAt) {
        AdminNotice notice = new AdminNotice();
        notice.setId(UUID.randomUUID().toString());
        notice.setRecipientId(recipientId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreatedAt(now);
        notice.setExpiresAt(expiresAt);
        return notice;
    }

    private static boolean admin(SysUser user) {
        return user.isEnabled() && user.getRoles().stream().anyMatch(
            role -> role.isEnabled() && ("R_SUPER".equals(role.getCode()) || "R_ADMIN".equals(role.getCode())));
    }

    private static boolean allowed(SysUser user, String permission) {
        return permission == null || user.getRoles().stream().filter(role -> role.isEnabled())
            .anyMatch(role -> "R_SUPER".equals(role.getCode()) || role.getPermissions().stream()
                .anyMatch(grant -> grant.isEnabled() && permission.equals(grant.getCode())));
    }

    private static String text(JsonNode node, String field) {
        String value = node.path(field).asText("").trim();
        return value.isEmpty() ? null : value;
    }
}
