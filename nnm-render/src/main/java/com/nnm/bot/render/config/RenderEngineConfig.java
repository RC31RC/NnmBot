package com.nnm.bot.render.config;

import com.sun.management.OperatingSystemMXBean;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.spi.RenderEngineProvider;
import com.nnm.bot.render.spi.RenderEngineProviderContext;
import com.nnm.bot.render.spi.RenderEngineProviderLoader;
import com.nnm.bot.render.spi.RenderPluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Selects an NnmBot render provider; external JARs are discovered through ServiceLoader. */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RenderProperties.class, RenderPluginProperties.class})
public class RenderEngineConfig {

    /** Keep enough headroom for Chromium; the 4GB deployment profile uses the lightweight engine. */
    private static final long PLAYWRIGHT_MIN_PHYSICAL_MEMORY = 8L * 1024 * 1024 * 1024;

    private final RenderProperties properties;
    private final List<RenderEngineProvider> providers;
    private final String configuredProvider;

    public RenderEngineConfig(RenderProperties properties,
                              RenderPluginProperties pluginProperties,
                              RenderEngineProviderLoader providerLoader) {
        properties.validate();
        this.properties = properties;
        this.providers = providerLoader.load(pluginProperties);
        this.configuredProvider = pluginProperties.getProvider();
    }

    @Bean
    public RenderEngine renderEngine() {
        RenderEngineProviderContext context = new RenderEngineProviderContext(
                "file:///templates/", properties.getPlaywright().getChromiumArgs());
        if (configuredProvider != null && !configuredProvider.isBlank()) {
            return create(configuredProvider.trim(), context);
        }
        return switch (properties.getEngine()) {
            case PLAYWRIGHT -> create("playwright", context);
            case OPENHTMLTOPDF -> create("openhtmltopdf", context);
            case AUTO -> autoSelect(context);
        };
    }

    private RenderEngine autoSelect(RenderEngineProviderContext context) {
        boolean enoughMemoryForPlaywright = hasEnoughMemoryForPlaywright();
        for (RenderEngineProvider provider : providers) {
            if (provider.id().equalsIgnoreCase("playwright") && !enoughMemoryForPlaywright) {
                continue;
            }
            try {
                if (provider.isAvailable(context)) {
                    log.info("Auto-selected [{}] render provider", provider.id());
                    return instantiate(provider, context);
                }
            } catch (RuntimeException | LinkageError e) {
                log.warn("Render provider [{}] is unavailable: {}", provider.id(), e.getMessage());
            }
        }
        return create("openhtmltopdf", context);
    }

    private RenderEngine create(String id, RenderEngineProviderContext context) {
        RenderEngineProvider provider = find(id);
        if (provider == null || !provider.isAvailable(context)) {
            throw new IllegalStateException("No available render provider: " + id);
        }
        return instantiate(provider, context);
    }

    private RenderEngine instantiate(RenderEngineProvider provider, RenderEngineProviderContext context) {
        return Objects.requireNonNull(provider.create(context),
                () -> "Render provider returned null: " + provider.id());
    }

    private RenderEngineProvider find(String id) {
        String normalized = id.toLowerCase(Locale.ROOT);
        return providers.stream()
                .filter(provider -> provider.id().trim().equalsIgnoreCase(normalized))
                .findFirst()
                .orElse(null);
    }

    private boolean hasEnoughMemoryForPlaywright() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            long totalPhysical = osBean.getTotalMemorySize();
            return totalPhysical >= PLAYWRIGHT_MIN_PHYSICAL_MEMORY;
        } catch (Throwable t) {
            log.warn("Unable to inspect physical memory; using a conservative JVM heap fallback: {}", t.getMessage());
            return Runtime.getRuntime().maxMemory() > 2L * 1024 * 1024 * 1024;
        }
    }
}
