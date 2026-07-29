package com.nnm.bot.render.spi;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class RenderEngineProviderLoaderTest {

    @Test
    void alwaysProvidesStableBuiltInFallbacksWhenExternalDirectoryIsAbsent() {
        RenderPluginProperties properties = new RenderPluginProperties();
        properties.setDirectory(Path.of("target/does-not-exist"));

        var providers = new RenderEngineProviderLoader().load(properties);

        assertThat(providers).extracting(RenderEngineProvider::id)
                .containsExactly("playwright", "openhtmltopdf");
        assertThat(providers).extracting(RenderEngineProvider::order)
                .isSorted();
    }
}
