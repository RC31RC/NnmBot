package com.nnm.bot.common.http;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpClientPropertiesTest {

    @Test
    void verifiesSslCertificatesByDefault() {
        assertThat(new HttpClientProperties().isTrustAllCerts()).isFalse();
    }

    @Test
    void validatesResourceAndRetryLimits() {
        HttpClientProperties properties = new HttpClientProperties();
        properties.setMaxAttempts(0);

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-attempts");

        properties.setMaxAttempts(3);
        properties.setRetryBackoff(Duration.ofSeconds(2));
        properties.setRetryMaxBackoff(Duration.ofSeconds(1));

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("retry-max-backoff");
    }
}
