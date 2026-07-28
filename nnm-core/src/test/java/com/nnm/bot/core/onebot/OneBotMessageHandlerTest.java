package com.nnm.bot.core.onebot;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.plugin.PluginDispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OneBotMessageHandlerTest {

    @Test
    void adaptsAndDispatchesAnyMessageEvent() {
        PluginDispatcher dispatcher = mock(PluginDispatcher.class);
        when(dispatcher.dispatch(any())).thenReturn(true);
        Bot bot = mock(Bot.class);
        when(bot.getSelfId()).thenReturn(10001L);
        AnyMessageEvent event = new AnyMessageEvent();
        event.setMessageType("group");
        event.setUserId(20002L);
        event.setGroupId(30003L);
        event.setRawMessage("帮助");

        boolean handled = new OneBotMessageHandler(dispatcher).onMessage(bot, event);

        ArgumentCaptor<BotMessageContext> captor = ArgumentCaptor.forClass(BotMessageContext.class);
        verify(dispatcher).dispatch(captor.capture());
        assertThat(handled).isTrue();
        assertThat(captor.getValue().message()).isEqualTo("帮助");
        assertThat(captor.getValue().groupId()).isEqualTo(30003L);
    }
}
