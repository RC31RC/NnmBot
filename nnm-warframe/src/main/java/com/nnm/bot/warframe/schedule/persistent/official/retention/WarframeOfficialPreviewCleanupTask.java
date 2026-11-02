package com.nnm.bot.warframe.schedule.persistent.official.retention;

import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import io.github.rdt.schedule.RdtScheduledTask;
import io.github.rdt.schedule.ScheduleDefinitionChangePolicy;
import io.github.rdt.schedule.ScheduleMisfirePolicy;
import io.github.rdt.schedule.ScheduleOptions;
import io.github.rdt.schedule.ScheduleSpec;
import io.github.rdt.schedule.TaskDescriptor;
import io.github.rdt.spring.annotation.RdtScheduled;
import jakarta.annotation.Resource;
import java.time.ZoneId;

/** 集群级回收已过期的官方预览及不再引用的快照对象。 */
@RdtScheduled(id = "warframe-official-preview-cleanup")
public class WarframeOfficialPreviewCleanupTask implements RdtScheduledTask {
    @Resource
    private WarframeOfficialRetentionService retention;

    /**
     * 声明官方预览对象的集群级回收周期。
     *
     * @return RDT 调度任务描述。
     */
    @Override
    public TaskDescriptor descriptor() {
        return new TaskDescriptor() {
            @Override
            public String displayName() {
                return "官方数据预览与快照清理";
            }

            @Override
            public ScheduleSpec schedule() {
                return new ScheduleSpec.Cron("0 */5 * * * *", ZoneId.of("Asia/Shanghai"));
            }

            @Override
            public ScheduleOptions options() {
                return ScheduleOptions.builder().maxAttempts(1).misfirePolicy(ScheduleMisfirePolicy.FIRE_ONCE)
                    .definitionChangePolicy(ScheduleDefinitionChangePolicy.UPDATE).build();
            }
        };
    }

    /** 清理过期对象；刷新容量校验只在显式刷新入口执行。 */
    @Override
    public void execute() {
        retention.cleanupExpired();
    }
}
