package com.nnm.bot.core.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BotMessageContextTest {

    @Test
    void mapsGroupMessageAndPrefersPlainText() {
        Bot bot = mock(Bot.class);
        when(bot.getSelfId()).thenReturn(10001L);
        AnyMessageEvent event = new AnyMessageEvent();
        event.setMessageType("group");
        event.setUserId(20002L);
        event.setGroupId(30003L);
        event.setMessageId(42);
        event.setRawMessage("[CQ:at,qq=10001] 帮助");
        event.setPlainText(" 帮助 ");

        BotMessageContext context = BotMessageContext.from(bot, event);

        assertThat(context.scope()).isEqualTo(BotMessageContext.Scope.GROUP);
        assertThat(context.botId()).isEqualTo(10001L);
        assertThat(context.userId()).isEqualTo(20002L);
        assertThat(context.groupId()).isEqualTo(30003L);
        assertThat(context.messageId()).isEqualTo(42);
        assertThat(context.rawMessage()).contains("CQ:at");
        assertThat(context.message()).isEqualTo("帮助");
    }

    @Test
    void fallsBackToRawMessageForPrivateText() {
        Bot bot = mock(Bot.class);
        AnyMessageEvent event = new AnyMessageEvent();
        event.setMessageType("private");
        event.setUserId(20002L);
        event.setRawMessage(" 状态 ");

        BotMessageContext context = BotMessageContext.from(bot, event);

        assertThat(context.scope()).isEqualTo(BotMessageContext.Scope.PRIVATE);
        assertThat(context.groupId()).isNull();
        assertThat(context.message()).isEqualTo("状态");
    }
}
