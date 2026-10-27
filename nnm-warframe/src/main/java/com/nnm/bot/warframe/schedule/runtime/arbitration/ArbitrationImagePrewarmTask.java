package com.nnm.bot.warframe.schedule.runtime.arbitration;

import com.nnm.bot.warframe.runtime.cache.arbitration.ArbitrationImagePrewarmer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日根据内存中最近成功解析的仲裁排期预生成图片。
 *
 * <p>图片结果属于当前 JVM 的本地缓存或本地文件，集群调度只让一个实例执行会导致
 * 其他实例仍然没有预热结果，因此这里保留每个实例各自执行的 Spring 调度。
 * 上游排期由独立刷新任务更新各实例内存快照，图片预热不能隐式发起上游请求。</p>
 */
@Slf4j
@Component
public class ArbitrationImagePrewarmTask {

    @Resource
    private ArbitrationImagePrewarmer imagePrewarmService;

    /**
     * 预热本实例的当日仲裁图片；渲染失败直接抛出，由调度框架记录。
     */
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    public void prewarm() {
        if (imagePrewarmService.prewarmDailyArbitrationImage()) {
            log.info("仲裁图片预热完成");
        }
    }
}
