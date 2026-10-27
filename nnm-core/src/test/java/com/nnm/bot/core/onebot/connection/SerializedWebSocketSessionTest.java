package com.nnm.bot.core.onebot.connection;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SerializedWebSocketSessionTest {
    @Test
    void losingOwnershipClosesSocketAndPreventsFurtherSends() throws Exception {
        WebSocketSession delegate = mock(WebSocketSession.class);
        when(delegate.isOpen()).thenReturn(true);
        AtomicBoolean held = new AtomicBoolean(true);
        SerializedWebSocketSession session = new SerializedWebSocketSession(delegate, held::get);
        TextMessage first = new TextMessage("first");
        session.sendMessage(first);
        verify(delegate).sendMessage(first);
        held.set(false);
        TextMessage stale = new TextMessage("stale");
        assertThatThrownBy(() -> session.sendMessage(stale)).isInstanceOf(IOException.class);
        verify(delegate, never()).sendMessage(stale);
        verify(delegate).close();
    }
}
