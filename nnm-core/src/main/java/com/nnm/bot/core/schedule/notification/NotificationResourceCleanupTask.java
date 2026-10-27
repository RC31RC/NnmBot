package com.nnm.bot.core.schedule.notification;

import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 仅负责本实例周期触发；状态与生命周期仍由所属组件维护。 */
@Component
public class NotificationResourceCleanupTask {
    @Resource
    private NotificationResourceStorage resources;

    @Scheduled(fixedDelayString = "#{@'nnm.bot.notifications-com.nnm.bot.core.config.BotNotificationProperties'.resourceCleanupInterval().toMillis()}")
    public void execute() {
        resources.deleteExpired();
    }
}
