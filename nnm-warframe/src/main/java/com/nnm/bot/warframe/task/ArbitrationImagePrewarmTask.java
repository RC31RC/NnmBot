package com.nnm.bot.warframe.task;

import com.nnm.bot.warframe.application.WorldStateImagePrewarmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日根据数据库中最近成功保存的仲裁排期预生成图片。
 *
 * <p>图片结果属于当前 JVM 的本地缓存或本地文件，集群调度只让一个实例执行会导致
 * 其他实例仍然没有预热结果，因此这里保留每个实例各自执行的 Spring 调度。
 * 外部数据刷新由独立的 RDT 集群任务负责，图片预热不能隐式发起上游请求。</p>
 */
@Slf4j
@Component
public class ArbitrationImagePrewarmTask {

    @Resource
    private WorldStateImagePrewarmService imagePrewarmService;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    public void prewarm() {
        try {
            if (imagePrewarmService.prewarmDailyArbitrationImage()) {
                log.info("仲裁图片预热完成");
            }
        } catch (Exception exception) {
            log.error("仲裁图片预热失败", exception);
        }
    }
}
