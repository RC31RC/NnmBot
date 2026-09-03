package com.nnm.bot.scheduler.task;

import com.nnm.bot.warframe.application.WorldStateImagePrewarmService;
import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.infrastructure.parser.WorldStateParser;
import com.nnm.bot.warframe.infrastructure.provider.WorldStateProvider;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日重置后刷新世界状态并预生成仲裁图片。
 */
@Slf4j
@Component
public class ArbitrationImagePrewarmTask {

    @Resource
    private WorldStateProvider worldStateProvider;
    @Resource
    private WorldStateService worldStateService;
    @Resource
    private WorldStateParser worldStateParser;
    @Resource
    private WorldStateImagePrewarmService imagePrewarmService;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    public void refreshAndPrewarm() {
        try {
            worldStateService.refresh(worldStateProvider, worldStateParser);
            if (imagePrewarmService.prewarmDailyArbitrationImage()) {
                log.info("Daily arbitration image prewarmed");
            }
        } catch (Exception exception) {
            log.error("Daily arbitration image prewarm failed", exception);
        }
    }
}
