package com.nnm.bot.core.onebot.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import com.mikuac.shiro.model.HandlerMethod;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;
import com.nnm.bot.core.plugin.execution.CommandIngressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketSession;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OneBotMessageHandlerTest {
    private final OneBotMessageHandler handler = new OneBotMessageHandler();
    private final CommandIngressService ingress = mock(CommandIngressService.class);
    private final Bot bot = mock(Bot.class);
    private final AnyMessageEvent event = new AnyMessageEvent();

    @BeforeEach
    void setup() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session");
        when(session.isOpen()).thenReturn(true);
        when(bot.getSession()).thenReturn(session);
        when(bot.getSelfId()).thenReturn(100L);
        event.setMessageType("group");
        event.setGroupId(200L);
        event.setUserId(300L);
        event.setMessageId(400);
        event.setRawMessage("command");
        OneBotMessageArrivalStore arrivals = mock(OneBotMessageArrivalStore.class);
        when(arrivals.receivedAt("session", "100", "400")).thenReturn(1_000L);
        ReflectionTestUtils.setField(handler, "messageArrivals", arrivals);
        ReflectionTestUtils.setField(handler, "commandIngressService", ingress);
    }

    @ParameterizedTest
    @EnumSource(value = CommandIngressResult.class, names = "NOT_MATCHED", mode = EnumSource.Mode.EXCLUDE)
    void acceptedAndRejectedCommandsStopTheFollowingSdkHandler(CommandIngressResult result) throws Exception {
        when(ingress.accept(any(), any())).thenReturn(result);
        NextHandler next = new NextHandler();
        assertThat(new InjectionHandler().invokeMessage(bot, event,
                Optional.of(List.of(sdkHandler(handler), sdkHandler(next))))).isTrue();
        assertThat(next.calls).isZero();
    }

    @Test
    void unmatchedContinuesButConversionOrMatchingFailureDoesNotPretendToBeUnmatched() {
        when(ingress.accept(any(), any())).thenReturn(CommandIngressResult.NOT_MATCHED);
        assertThat(handler.onMessage(bot, event)).isFalse();
        IllegalStateException failure = new IllegalStateException("match failed");
        when(ingress.accept(any(), any())).thenThrow(failure);
        assertThatThrownBy(() -> handler.onMessage(bot, event)).isSameAs(failure);
    }

    @Test
    void unmatchedContinuesThroughTheActualShiroHandlerChain() throws Exception {
        when(ingress.accept(any(), any())).thenReturn(CommandIngressResult.NOT_MATCHED);
        NextHandler next = new NextHandler();
        assertThat(new InjectionHandler().invokeMessage(bot, event,
                Optional.of(List.of(sdkHandler(handler), sdkHandler(next))))).isTrue();
        assertThat(next.calls).isOne();
    }

    private HandlerMethod sdkHandler(Object target) throws NoSuchMethodException {
        HandlerMethod method = new HandlerMethod();
        method.setObject(target);
        method.setMethod(target.getClass().getMethod("onMessage", Bot.class, AnyMessageEvent.class));
        return method;
    }

    public static class NextHandler {
        private int calls;

        public boolean onMessage(Bot bot, AnyMessageEvent event) {
            calls++;
            return true;
        }
    }
}
