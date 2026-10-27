package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.message.model.BotProactiveTarget;
import java.util.UUID;

/** owner 节点在每次平台提交前重新校验任务授权与当前群组绑定。 */
public interface NotificationDeliveryGuard {
    void validate(UUID submissionId, BotProactiveTarget target);
}
