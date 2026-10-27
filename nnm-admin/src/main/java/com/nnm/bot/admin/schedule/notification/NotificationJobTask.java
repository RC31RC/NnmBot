package com.nnm.bot.admin.schedule.notification;

import com.nnm.bot.admin.config.NotificationJobProperties;
import com.nnm.bot.admin.service.bot.notification.BotNotificationJobService;
import io.github.rdt.schedule.RdtScheduledTask;
import io.github.rdt.schedule.ScheduleDefinitionChangePolicy;
import io.github.rdt.schedule.ScheduleMisfirePolicy;
import io.github.rdt.schedule.ScheduleOptions;
import io.github.rdt.schedule.ScheduleSpec;
import io.github.rdt.schedule.TaskDescriptor;
import io.github.rdt.spring.annotation.RdtScheduled;
import jakarta.annotation.Resource;
import java.time.ZoneId;

@RdtScheduled(id = "bot-notification-jobs-v1")
public class NotificationJobTask implements RdtScheduledTask {
    @Resource
    private BotNotificationJobService jobs;
    @Resource
    private NotificationJobProperties properties;
    @Override
    public TaskDescriptor descriptor() {
        return new TaskDescriptor() {
            @Override
            public String displayName() { return "通知到期投递与任务清理"; }
            @Override
            public ScheduleSpec schedule() { return new ScheduleSpec.Cron(properties.getCron(), ZoneId.of(properties.getZone())); }
            @Override
            public ScheduleOptions options() {
                return ScheduleOptions.builder().maxAttempts(1).misfirePolicy(ScheduleMisfirePolicy.FIRE_ONCE)
                        .definitionChangePolicy(ScheduleDefinitionChangePolicy.UPDATE).build();
            }
        };
    }
    @Override
    public void execute() { jobs.dispatchDue(); }
}
