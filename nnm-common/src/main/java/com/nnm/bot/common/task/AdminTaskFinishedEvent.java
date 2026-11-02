package com.nnm.bot.common.task;

/** 任务终态与原有权限一起传播；跨线程消费者不依赖 Web 线程上下文。 */
public record AdminTaskFinishedEvent(AdminTaskEvent event, String requiredPermission) {
}
