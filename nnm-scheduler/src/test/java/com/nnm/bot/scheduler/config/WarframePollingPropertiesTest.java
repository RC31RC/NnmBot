package com.nnm.bot.scheduler.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarframePollingPropertiesTest {

    @Test
    void usesSingleNodeDefaults() {
        WarframePollingProperties properties = new WarframePollingProperties();

        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getInterval()).isEqualTo(Duration.ofMinutes(2));
    }

    @Test
    void rejectsNonPositiveInterval() {
        WarframePollingProperties properties = new WarframePollingProperties();
        properties.setInterval(Duration.ZERO);

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("polling.interval");
    }
}
