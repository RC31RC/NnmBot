package com.nnm.bot.core.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class PluginDispatcher {

    private final List<BotPlugin> plugins;

    public PluginDispatcher(List<BotPlugin> plugins) {
        this.plugins = plugins.stream()
                .sorted(Comparator.comparingInt(BotPlugin::order))
                .toList();
        log.info("Loaded {} bot plugins", plugins.size());
    }

    public void dispatch(Bot bot, AnyMessageEvent event, String rawMessage) {
        for (BotPlugin plugin : plugins) {
            if (plugin.match(rawMessage)) {
                try {
                    plugin.handle(bot, event, rawMessage);
                } catch (Exception e) {
                    log.error("Plugin [{}] execution failed", plugin.command(), e);
                }
                return;
            }
        }
    }
}
