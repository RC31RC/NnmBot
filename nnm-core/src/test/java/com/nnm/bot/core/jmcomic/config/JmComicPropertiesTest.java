package com.nnm.bot.core.jmcomic.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JmComicPropertiesTest {

    @Test
    void shouldAcceptDefaultResourceLimits() {
        JmComicProperties properties = new JmComicProperties();
        properties.validate();

        assertThat(properties.isWholeFileEnabled()).isTrue();
        assertThat(properties.isPageDownloadEnabled()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    void shouldBindDownloadSwitchesIndependently(boolean wholeFile, boolean pages) {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
                "nnm.jmcomic.whole-file-enabled", wholeFile,
                "nnm.jmcomic.page-download-enabled", pages));
        JmComicProperties properties = new Binder(source)
                .bind("nnm.jmcomic", Bindable.of(JmComicProperties.class))
                .orElseThrow(IllegalStateException::new);

        properties.validate();
        assertThat(properties.isWholeFileEnabled()).isEqualTo(wholeFile);
        assertThat(properties.isPageDownloadEnabled()).isEqualTo(pages);
    }

    @Test
    void shouldUseProjectDataDirectoryByDefault() {
        JmComicProperties properties = new JmComicProperties();

        assertThat(properties.getTemporaryDirectory())
                .isEqualTo(Path.of(System.getProperty("user.dir"), "data", "jmcomic"));
    }

    @Test
    void shouldRejectIncompleteProxyConfiguration() {
        JmComicProperties properties = new JmComicProperties();
        properties.setProxyHost("127.0.0.1");

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("proxy-host and proxy-port");
    }

    @Test
    void shouldRejectNonPositiveTimeout() {
        JmComicProperties properties = new JmComicProperties();
        properties.setRequestTimeout(Duration.ZERO);

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("request-timeout");
    }

    @Test
    void shouldRejectIncompleteAccountConfiguration() {
        JmComicProperties properties = new JmComicProperties();
        properties.setUsername("test-user");

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("username and password");
    }
}
