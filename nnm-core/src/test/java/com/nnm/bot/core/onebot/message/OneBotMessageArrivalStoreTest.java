package com.nnm.bot.core.onebot.message;

import com.nnm.bot.core.config.BotCacheProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class OneBotMessageArrivalStoreTest {
    @Test
    void preservesFirstLocalArrivalForReplaysAndSeparatesConnectionGenerations() {
        OneBotMessageArrivalStore store = new OneBotMessageArrivalStore();
        BotCacheProperties properties = new Binder(new MapConfigurationPropertySource(Map.of()))
                .bindOrCreate("nnm.cache.bot", BotCacheProperties.class);
        ReflectionTestUtils.setField(store, "properties", properties);
        store.initialize();
        try {
            store.record("session-1", "bot", "message", 1000);
            store.record("session-1", "bot", "message", 2000);
            store.record("session-2", "bot", "message", 3000);
            assertThat(store.receivedAt("session-1", "bot", "message")).isEqualTo(1000);
            assertThat(store.receivedAt("session-1", "bot", "message")).isEqualTo(1000);
            assertThat(store.receivedAt("session-2", "bot", "message")).isEqualTo(3000);
            assertThatThrownBy(() -> store.receivedAt("missing", "bot", "message"))
                    .isInstanceOf(IllegalStateException.class);
        } finally {
            store.close();
        }
    }
}
