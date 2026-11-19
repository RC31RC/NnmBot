package com.nnm.bot.core.task.event;

import com.nnm.bot.core.task.pojo.dto.AdminTaskDTO;

/** 任务终态与原有权限一起传播；跨线程消费者不依赖 Web 线程上下文。 */
public record AdminTaskFinishedEvent(AdminTaskDTO event, String requiredPermission) {
}
