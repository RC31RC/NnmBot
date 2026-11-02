package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.util.List;

/** 与群组发现并列的消息读取端口，能力按具体会话及实际连接判断。 */
public interface BotMessageReadProvider {

    /** 返回本实现所属的平台。 */
    BotPlatform platform();

    /** 当前会话是否提供远程原消息查询；不等同于可以读取事件已带的引用。 */
    boolean canReadReference(BotMessageContext context);

    /** 按原接收账号读取一层引用的附件，不递归展开引用。 */
    List<BotIncomingAttachment> readReference(BotMessageContext context);

    /** 将平台文件标识解析为可下载的图片引用，已有合法 URL 时直接返回。 */
    BotIncomingAttachment resolveAttachment(BotMessageContext context, BotIncomingAttachment attachment);
}
