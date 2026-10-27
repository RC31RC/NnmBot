package com.nnm.bot.core.group.service;

import com.nnm.bot.core.plugin.model.ReplyBlock.DataView;
import java.nio.charset.StandardCharsets;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.PointCommandDisplay;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.ArrayList;

/**
 * 集中定义命令积分的幂等和响应规则，避免特殊命令把记账逻辑带回插件。
 */
@Component
public class CommandPointPolicy {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");

    public String costIdempotencyKey(BotMessageContext context,
                                     CommandDescriptor descriptor,
                                     long groupId,
                                     long memberId) {
        return messageIdempotencyKey(
                "COMMAND_COST", context, descriptor, groupId, memberId);
    }

    public String rewardIdempotencyKey(BotMessageContext context,
                                       CommandDescriptor descriptor,
                                       long groupId,
                                       long memberId) {
        if (isSignIn(descriptor)) {
            // 沿用历史键格式，升级当天不会把旧签到记录再次奖励。
            return "SIGN_IN:" + groupId + ":" + memberId + ":"
                    + LocalDate.now(BUSINESS_ZONE);
        }
        return messageIdempotencyKey(
                "COMMAND_REWARD", context, descriptor, groupId, memberId);
    }

    public boolean allowsRepeatedReward(CommandDescriptor descriptor) {
        return isSignIn(descriptor);
    }

    public PointOperation rewardOperation(long groupId,
                                          long memberId,
                                          CommandDescriptor descriptor,
                                          long rewardPoints,
                                          String idempotencyKey) {
        boolean signIn = isSignIn(descriptor);
        return new PointOperation(
                groupId,
                memberId,
                descriptor.code(),
                signIn ? "SIGN_IN" : "COMMAND_REWARD",
                rewardPoints,
                idempotencyKey,
                signIn ? "群组签到" : "命令成功奖励");
    }

    public CommandResult completeReward(CommandDescriptor descriptor,
                                        CommandResult result,
                                        PointChangeResult change) {
        if (!isSignIn(descriptor)) {
            return result;
        }
        PointBalance balance = change.balance();
        PointCommandDisplay data = new PointCommandDisplay(balance.points(), balance.totalEarned(), balance.totalSpent());
        String text = change.applied()
                ? "签到成功，当前积分：" + balance.points()
                : "今天已经签到，当前积分：" + balance.points();
        ArrayList<ReplyMessage> replies = new ArrayList<>(result.replies());
        if (replies.isEmpty()) throw new IllegalStateException("Sign-in result requires a balance view");
        ReplyMessage reply = replies.getFirst();
        ArrayList<ReplyBlock> blocks = new ArrayList<>(reply.blocks());
        if (blocks.getFirst() instanceof ReplyBlock.Text) blocks.set(0, new ReplyBlock.Text(text));
        else blocks.addFirst(new ReplyBlock.Text(text));
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i) instanceof DataView<?> view && view.data() instanceof PointCommandDisplay) {
                blocks.set(i, new ReplyBlock.DataView<>(view.viewKey(), data, view.summary()));
            }
        }
        if (blocks.stream().noneMatch(block -> block instanceof DataView<?> view
                && view.data() instanceof PointCommandDisplay)) {
            blocks.add(new ReplyBlock.DataView<>("system.points", data, null));
        }
        replies.set(0, new ReplyMessage(blocks, reply.actions(), reply.composition(), reply.relatedActions()));
        return result.withReplies(replies);
    }

    private String messageIdempotencyKey(String type,
                                         BotMessageContext context,
                                         CommandDescriptor descriptor,
                                         long groupId,
                                         long memberId) {
        String eventId = context.messageId();
        if (StrUtil.isBlank(eventId)) {
            String fallback = context.rawMessage() + "|" + context.senderId()
                    + "|" + context.conversationId();
            eventId = UUID.nameUUIDFromBytes(fallback.getBytes(
                    StandardCharsets.UTF_8)).toString();
        }
        String key = type + ":" + groupId + ":" + memberId + ":"
                + context.platform().name() + ":" + descriptor.code() + ":" + eventId;
        if (key.length() <= 160) {
            return key;
        }
        String digest = UUID.nameUUIDFromBytes(key.getBytes(
                StandardCharsets.UTF_8)).toString();
        return type + ":" + groupId + ":" + memberId + ":" + digest;
    }

    private boolean isSignIn(CommandDescriptor descriptor) {
        return SystemCommandCodes.SIGN_IN.equalsIgnoreCase(descriptor.code());
    }
}
