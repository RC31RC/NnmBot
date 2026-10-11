package com.nnm.bot.warframe.messaging.consumer;

import com.nnm.bot.warframe.application.WarframeWorldStateService;
import io.github.rdt.queue.QueueMode;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/**
 * 消费集群共享的 Warframe 世界状态刷新请求。
 */
@RdtQueueListener(
        value = "worldstate-refresh-v1",
        displayName = "Warframe 世界状态刷新",
        mode = QueueMode.STREAM,
        group = "worldstate-workers-v1"
)
public class WarframeWorldStateRefreshMessageConsumer
        implements RdtMessageConsumer<String> {
    @Resource
    private WarframeWorldStateService worldStateService;

    /**
     * 执行最新世界状态刷新。
     *
     * <p>重复消息只会重新拉取当前数据，不会重放历史快照。</p>
     *
     * @param command 当前支持的刷新命令
     */
    @Override
    public void consume(String command) {
        if (!"latest".equals(command)) {
            throw new IllegalArgumentException("不支持的 Warframe 世界状态刷新命令: " + command);
        }
        worldStateService.refresh();
    }
}
