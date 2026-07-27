package com.nnm.bot.common.retry;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetryBackoffTest {

    @Test
    void calculatesExponentialDelayWithUpperBound() {
        Duration initial = Duration.ofMillis(500);
        Duration maximum = Duration.ofSeconds(2);

        assertThat(RetryBackoff.exponential(initial, maximum, 1))
                .isEqualTo(Duration.ofMillis(500));
        assertThat(RetryBackoff.exponential(initial, maximum, 2))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(RetryBackoff.exponential(initial, maximum, 10))
                .isEqualTo(maximum);
    }

    @Test
    void rejectsInvalidArguments() {
        assertThatThrownBy(() -> RetryBackoff.exponential(
                Duration.ofSeconds(2),
                Duration.ofSeconds(1),
                1
        )).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RetryBackoff.exponential(
                Duration.ofSeconds(1),
                Duration.ofSeconds(2),
                0
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
