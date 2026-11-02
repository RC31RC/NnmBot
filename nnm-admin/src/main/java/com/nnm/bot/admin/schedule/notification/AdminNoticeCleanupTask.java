package com.nnm.bot.admin.schedule.notification;

import com.nnm.bot.admin.service.system.notification.AdminNoticeService;
import io.github.rdt.schedule.RdtScheduledTask;
import io.github.rdt.schedule.ScheduleOptions;
import io.github.rdt.schedule.ScheduleSpec;
import io.github.rdt.schedule.TaskDescriptor;
import io.github.rdt.spring.annotation.RdtScheduled;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.ZoneId;

/** 已过期的管理通知按小时清理，审核权限立即由预览自身的截止时间控制。 */
@RdtScheduled(id = "admin-notice-cleanup")
public class AdminNoticeCleanupTask implements RdtScheduledTask {
    @Resource
    private AdminNoticeService notices;

    @Override
    public TaskDescriptor descriptor() {
        return new TaskDescriptor() {
            @Override
            public String displayName() {
                return "管理通知到期清理";
            }

            @Override
            public ScheduleSpec schedule() {
                return new ScheduleSpec.Cron("0 0 * * * *", ZoneId.of("Asia/Shanghai"));
            }

            @Override
            public ScheduleOptions options() {
                return ScheduleOptions.builder().build();
            }
        };
    }

    @Override
    public void execute() {
        notices.cleanup(Instant.now());
    }
}
