package com.nnm.bot.warframe.schedule.persistent.official.image;

import com.nnm.bot.warframe.persistent.official.config.image.WarframeImageDataProperties;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 在每个实例中刷新基于已采纳官方资料构建的图片索引。 */
@Component
public class WarframeOfficialImageIndexRefreshTask {

    @Resource
    private WarframeImageResolver imageResolver;
    @Resource
    private WarframeImageDataProperties properties;

    /**
     * 按配置重建本实例的图片索引。
     */
    @Scheduled(
            initialDelayString = "#{@warframeImageDataProperties.initialDelay.toString()}",
            fixedDelayString = "#{@warframeImageDataProperties.refreshCheckInterval.toString()}")
    public void refresh() {
        if (properties.isEnabled()) {
            imageResolver.refresh();
        }
    }
}
