package com.nnm.bot.core.messaging.sender;

import com.nnm.bot.core.messaging.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.channel.constant.BotChannelCapability;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.messaging.model.BotMessagePart;
import com.nnm.bot.core.messaging.model.PreparedBotMessage;
import com.nnm.bot.core.messaging.model.BotMessageSendPermit;
import java.util.List;
import java.util.stream.IntStream;

/** 准备并发送单个平台的消息内容，平台细节由适配器实现。 */
public interface BotMessageSender {

    /** 返回此发送器所属的平台。 */
    BotPlatform platform();

    /**
     * 判断当前上下文和能力是否可用于发送。
     *
     * @param context 目标消息上下文
     * @param capability 所需渠道能力
     * @return 目标连接具备该能力时为 {@code true}
     */
    boolean supports(BotMessageContext context, BotChannelCapability capability);

    /**
     * 判断一条消息包含的内容类型能否按顺序组合发送。
     *
     * @param context 目标消息上下文
     * @param parts 按展示顺序排列的消息部分类型
     * @return 平台支持此组合时为 {@code true}
     */
    boolean supportsComposition(BotMessageContext context, List<PartKind> parts);

    /** 预检是否可为指定数量的消息预留额度。 */
    default boolean canReserve(BotMessageContext context, int count) {
        return count > 0;
    }

    /**
     * 整组预留必须原子完成；无协议额度的平台只提供生命周期句柄。
     *
     * @param context 目标消息上下文
     * @param count 整组消息数
     * @return 与消息一一对应的许可
     * @throws IllegalArgumentException count 小于 1
     */
    default List<BotMessageSendPermit> reserve(BotMessageContext context, int count) {
        if (count < 1)
            throw new IllegalArgumentException("count must be positive");
        return IntStream.range(0, count).mapToObj(index -> BotMessageSendPermit.unrestricted()).toList();
    }

    /**
     * 可上传或暂存资源，但不得创建平台消息；调用方负责关闭返回对象。
     *
     * @param context 目标消息上下文
     * @param blocks 按顺序排列的待发送内容
     * @param mentionUserId 可选提及用户标识
     * @param permit 对应消息的协议发送许可
     * @return 已准备但尚未创建平台消息的资源
     */
    PreparedBotMessage prepare(BotMessageContext context, List<BotMessagePart> blocks, String mentionUserId,
        BotMessageSendPermit permit);
}
