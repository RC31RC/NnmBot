package com.nnm.bot.core.group.service;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.GroupPointConstants.TransactionType;
import com.nnm.bot.core.constant.SystemCommandCodes;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.PointCommandDisplay;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 集中定义命令积分的幂等和响应规则，避免特殊命令把记账逻辑带回插件。
 */
@Component
public class CommandPointPolicy {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");

    public String costIdempotencyKey(BotMessageContext context, CommandDescriptor descriptor, long groupId,
        long memberId) {
        return messageIdempotencyKey(TransactionType.COMMAND_COST, context, descriptor, groupId, memberId);
    }

    public String rewardIdempotencyKey(BotMessageContext context, CommandDescriptor descriptor, long groupId,
        long memberId) {
        if (isSignIn(descriptor)) {
            // 签到是按业务日期幂等；其他奖励按消息或调用身份幂等。
            return TransactionType.SIGN_IN.name() + ":" + groupId + ":" + memberId + ":" + LocalDate.now(BUSINESS_ZONE);
        }
        return messageIdempotencyKey(TransactionType.COMMAND_REWARD, context, descriptor, groupId, memberId);
    }

    public boolean allowsRepeatedReward(CommandDescriptor descriptor) {
        return isSignIn(descriptor);
    }

    public PointOperation rewardOperation(long groupId, long memberId, CommandDescriptor descriptor, long rewardPoints,
        String idempotencyKey) {
        boolean signIn = isSignIn(descriptor);
        return new PointOperation(groupId, memberId, descriptor.code(),
            signIn ? TransactionType.SIGN_IN : TransactionType.COMMAND_REWARD, rewardPoints, idempotencyKey,
            signIn ? "群组签到" : "命令成功奖励");
    }

    public CommandResult completeReward(CommandDescriptor descriptor, CommandResult result, PointChangeResult change) {
        if (!isSignIn(descriptor)) {
            return result;
        }
        PointBalance balance = change.balance();
        PointCommandDisplay data =
            new PointCommandDisplay(balance.points(), balance.totalEarned(), balance.totalSpent());
        String text = change.applied() ? "签到成功，当前积分：" + balance.points() : "今天已经签到，当前积分：" + balance.points();
        ArrayList<ReplyBlock> blocks = new ArrayList<>(result.blocks());
        if (blocks.getFirst() instanceof ReplyBlock.Text)
            blocks.set(0, new ReplyBlock.Text(text));
        else
            blocks.addFirst(new ReplyBlock.Text(text));
        int htmlDataIndex = -1;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i) instanceof ReplyBlock.HtmlData<?>) {
                htmlDataIndex = i;
                break;
            }
        }
        if (htmlDataIndex < 0)
            blocks.add(new ReplyBlock.HtmlData<>(data));
        else
            blocks.set(htmlDataIndex, new ReplyBlock.HtmlData<>(data));
        return new CommandResult(result.status(), blocks, result.actionContext());
    }

    private String messageIdempotencyKey(TransactionType type, BotMessageContext context, CommandDescriptor descriptor,
        long groupId, long memberId) {
        String eventId = context.messageId();
        if (StrUtil.isBlank(eventId)) {
            // 没有平台事件身份时，不把相同文本永久当成同一条消息。
            eventId = context.invocationId();
        }
        String key = type + ":" + groupId + ":" + memberId + ":" + context.platform().name() + ":" + descriptor.code()
            + ":" + eventId;
        if (key.length() <= 160) {
            return key;
        }
        String digest = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
        return type + ":" + groupId + ":" + memberId + ":" + digest;
    }

    private boolean isSignIn(CommandDescriptor descriptor) {
        return SystemCommandCodes.SIGN_IN.equalsIgnoreCase(descriptor.code());
    }
}
