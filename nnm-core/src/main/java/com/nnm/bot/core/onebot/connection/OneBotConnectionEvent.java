package com.nnm.bot.core.onebot.connection;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.CoreEvent;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotPlatform;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Primary
@Component
public class OneBotConnectionEvent extends CoreEvent {

    private static final Logger log = LoggerFactory.getLogger(OneBotConnectionEvent.class);
    @Resource
    private BotChannelRegistry channelRegistry;

    @Override
    public void online(Bot bot) {
        this.channelRegistry.connected(key(bot.getSelfId()));
        log.info("OneBot connected, botId={}", bot.getSelfId());
    }

    @Override
    public void offline(long account) {
        this.channelRegistry.disconnected(key(account));
        log.warn("OneBot disconnected, botId={}", account);
    }

    @Override
    public boolean session(WebSocketSession session) {
        log.debug("OneBot WebSocket session established, remoteAddress={}", session.getRemoteAddress());
        return true;
    }

    private static BotChannelKey key(long account) {
        return new BotChannelKey(BotPlatform.ONEBOT, String.valueOf(account));
    }
}
