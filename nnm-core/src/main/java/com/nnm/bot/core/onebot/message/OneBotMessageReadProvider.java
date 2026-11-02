package com.nnm.bot.core.onebot.message;

import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.handler.ActionHandler;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.port.BotMessageReadProvider;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.BotMessageConstants.ReferenceKind;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** OneBot 原消息和图片文件标识读取，沿用 SDK 的回执等待与当前连接。 */
@Component
public class OneBotMessageReadProvider implements BotMessageReadProvider {

    /** SDK 允许扩展动作，不另建 WebSocket 请求链。 */
    private static final String GET_IMAGE = "get_image";
    @Resource
    private ActionHandler actions;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public boolean canReadReference(BotMessageContext context) {
        return context.reference() != null && context.reference().kind() == ReferenceKind.MESSAGE_ID;
    }

    @Override
    public List<BotIncomingAttachment> readReference(BotMessageContext context) {
        String id = context.reference().id();
        if (!id.matches("-?[0-9]{1,10}"))
            throw unavailable();
        long parsed = Long.parseLong(id);
        if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE)
            throw unavailable();
        Bot bot = context.nativeSource(OneBotMessageSource.class).bot();
        ActionData<MsgResp> response = bot.getMsg((int)parsed);
        if (response == null || !"ok".equalsIgnoreCase(response.getStatus()) || response.getData() == null) {
            throw unavailable();
        }
        // 标准 get_msg 没有保证返回 group_id；只接受当前协议事件提供的显式引用。
        return OneBotMessageContextFactory.attachments(response.getData().getArrayMsg());
    }

    @Override
    public BotIncomingAttachment resolveAttachment(BotMessageContext context, BotIncomingAttachment attachment) {
        if (attachment.fileId() == null)
            throw unavailable();
        Bot bot = context.nativeSource(OneBotMessageSource.class).bot();
        JsonObjectWrapper response =
            actions.action(bot.getSession(), () -> GET_IMAGE, Map.of("file", attachment.fileId()));
        if (response == null || !"ok".equalsIgnoreCase(response.getString("status")))
            throw unavailable();
        // SDK 使用 Jackson 3，保留协议库类型在适配器内，不泄漏到核心模型。
        var data = response.raw().path("data");
        String url = data.path("url").asString("");
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            throw unavailable();
        return new BotIncomingAttachment(attachment.type(), attachment.fileName(), attachment.contentType(), url,
            attachment.fileId(), attachment.size(), attachment.width(), attachment.height(), attachment.metadata());
    }

    /** 不把远端文件系统路径当作当前服务器的本地路径读取。 */
    private BusinessException unavailable() {
        return new BusinessException(BotChannelErrorCode.REFERENCED_IMAGE_UNAVAILABLE);
    }
}
