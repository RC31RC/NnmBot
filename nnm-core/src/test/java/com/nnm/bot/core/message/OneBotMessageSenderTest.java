package com.nnm.bot.core.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OneBotMessageSenderTest {

    private final OneBotMessageSender sender = new OneBotMessageSender();

    @Test
    void sendsTextAsEscapedPlainText() {
        Bot bot = mock(Bot.class);
        AnyMessageEvent event = event();
        when(bot.sendMsg(event, "ok", true)).thenReturn(success());

        sender.sendText(BotMessageContext.from(bot, event), "ok");

        verify(bot).sendMsg(event, "ok", true);
    }

    @Test
    void sendsImageAsCqMessage() {
        Bot bot = mock(Bot.class);
        AnyMessageEvent event = event();
        when(bot.sendMsg(event, "[CQ:image,file=base64://AQID]", false)).thenReturn(success());

        sender.sendImage(BotMessageContext.from(bot, event), new byte[]{1, 2, 3});

        verify(bot).sendMsg(event, "[CQ:image,file=base64://AQID]", false);
    }

    @Test
    void rejectsFailedOneBotResponse() {
        Bot bot = mock(Bot.class);
        AnyMessageEvent event = event();
        ActionData<MsgId> failed = new ActionData<>();
        failed.setStatus("failed");
        failed.setRetCode(100);
        when(bot.sendMsg(event, "test", true)).thenReturn(failed);

        assertThatThrownBy(() -> sender.sendText(BotMessageContext.from(bot, event), "test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OneBot message send failed");
    }

    private static AnyMessageEvent event() {
        AnyMessageEvent event = new AnyMessageEvent();
        event.setMessageType("private");
        event.setUserId(1L);
        event.setRawMessage("test");
        return event;
    }

    private static ActionData<MsgId> success() {
        ActionData<MsgId> result = new ActionData<>();
        result.setStatus("ok");
        result.setRetCode(0);
        return result;
    }
}
