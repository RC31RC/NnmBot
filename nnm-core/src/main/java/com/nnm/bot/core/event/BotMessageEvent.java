package com.nnm.bot.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BotMessageEvent extends ApplicationEvent {

    private final long groupId;
    private final byte[] image;
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
        return image != null && image.length > 0;
    }
}
