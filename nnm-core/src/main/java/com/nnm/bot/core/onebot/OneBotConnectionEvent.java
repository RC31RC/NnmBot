package com.nnm.bot.core.onebot;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.CoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Primary
@Component
public class OneBotConnectionEvent extends CoreEvent {

    private static final Logger log = LoggerFactory.getLogger(OneBotConnectionEvent.class);

    @Override
    public void online(Bot bot) {
        log.info("OneBot connected, botId={}", bot.getSelfId());
    }

    @Override
    public void offline(long account) {
        log.warn("OneBot disconnected, botId={}", account);
    }

    @Override
    public boolean session(WebSocketSession session) {
        log.debug("OneBot WebSocket session established, remoteAddress={}", session.getRemoteAddress());
        return true;
    }
}
