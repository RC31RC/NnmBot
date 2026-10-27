package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;

/**
 * 旧集群认领入口，保留给历史调用方迁移；不再装配，也不读写 Redis。
 * 当前生产链路直接使用 CommandReceiptRegistry，跨节点接管允许重新开始本地窗口。
 */
@Deprecated(forRemoval = true)
public class ClusterMessageClaimService {
    @Resource
    private CommandReceiptRegistry receipts;

    public boolean claim(BotMessageContext context) {
        return receipts.claimLocal(context);
    }

    public boolean isReplay(BotMessageContext context) {
        return receipts.isReplay(context);
    }
}
