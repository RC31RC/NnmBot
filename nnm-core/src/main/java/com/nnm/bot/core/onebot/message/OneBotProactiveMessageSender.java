package com.nnm.bot.core.onebot.message;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/** OneBot 主动群消息适配器，支持在同一条消息中提醒多个 QQ 用户。 */
@Component
public class OneBotProactiveMessageSender implements BotProactiveMessageSender {

    @Resource
    private BotContainer botContainer;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public void sendGroupText(BotProactiveGroupMessage message) {
        Assert.notNull(message, "message must not be null");
        Assert.hasText(message.text(), "message.text must not be blank");
        long botAccountId = parseId(message.botAccountId(), "机器人账号");
        long groupId = parseId(message.conversationId(), "群组标识");
        Bot bot = botContainer.robots.get(botAccountId);
        if (bot == null) {
            throw new BusinessException(
                    BotChannelErrorCode.CHANNEL_NOT_REGISTERED,
                    "OneBot 机器人当前未连接: " + message.botAccountId());
        }

        MsgUtils builder = MsgUtils.builder();
        for (String userId : message.mentionUserIds()) {
            if (StrUtil.isNotBlank(userId)) {
                try {
                    builder.at(Long.parseLong(userId));
                } catch (NumberFormatException ignored) {
                    // OneBot 的 @ 只接受数字 QQ 号，无法使用的身份不影响正文发送。
                }
            }
        }
        ActionData<MsgId> result = bot.sendGroupMsg(
                groupId, builder.text(message.text()).build(), false);
        ensureSuccess(result);
    }

    private static long parseId(String value, String name) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.INVALID_ACCOUNT_ID,
                    name + "不是有效数字: " + value,
                    exception);
        }
    }

    private static void ensureSuccess(ActionData<MsgId> result) {
        if (ObjectUtil.isNull(result)
                || !"ok".equalsIgnoreCase(result.getStatus())
                || (ObjectUtil.isNotNull(result.getRetCode()) && result.getRetCode() != 0)) {
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SEND_FAILED,
                    "OneBot 主动群消息发送失败: " + result);
        }
    }
}
