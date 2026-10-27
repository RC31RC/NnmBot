package com.nnm.bot.core.channel.topology;

import com.nnm.bot.core.mq.message.topology.BotTopologyRefreshMessage;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
public class BotTopologyInvalidator {
    @Resource
    private BotTopologyStore topology;
    @Resource
    private RdtMessageSendManager messages;

    public void invalidateAfterCommit() {
        Runnable refresh = () -> {
            String version = "invalidate:" + UUID.randomUUID();
            try {
                topology.refresh();
                version = topology.snapshot().version();
            } catch (RuntimeException failure) {
                // 数据已提交，刷新故障不能把成功写入伪装成失败；仍通知其他节点并由周期校验补偿。
                log.error("配置已提交，本节点拓扑刷新失败，等待周期校验", failure);
            }
            try {
                messages.send(new BotTopologyRefreshMessage(version));
            } catch (RuntimeException failure) {
                log.error("配置已提交，拓扑版本广播失败，等待各节点周期校验", failure);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    refresh.run();
                }
            });
        } else {
            refresh.run();
        }
    }
}
