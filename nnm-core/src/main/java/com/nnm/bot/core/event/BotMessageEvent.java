package com.nnm.bot.core.event;

import cn.hutool.core.util.ArrayUtil;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 遗留的 Spring 消息事件模型。
 *
 * <p>当前命令主链路使用 {@code BotMessageContext} 直接进入插件分发，
 * 该事件暂时保留用于兼容旧调用方和后续迁移评估，不参与新的命令分发。</p>
 */
@Getter
public class BotMessageEvent extends ApplicationEvent {

    /** 事件对应的群组标识。 */
    private final long groupId;
    /** 事件携带的图片内容；文字事件中为空。 */
    private final byte[] image;
    /** 事件携带的文字内容；图片事件中为空。 */
    private final String textMessage;

    public BotMessageEvent(Object source, long groupId, byte[] image) {
        super(source);
        this.groupId = groupId;
        this.image = image;
        this.textMessage = null;
    }

    public BotMessageEvent(Object source, long groupId, String textMessage) {
        super(source);
        this.groupId = groupId;
        this.image = null;
        this.textMessage = textMessage;
    }

    public boolean hasImage() {
        return ArrayUtil.isNotEmpty(image);
    }
}
