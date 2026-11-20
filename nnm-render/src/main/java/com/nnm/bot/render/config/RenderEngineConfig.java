package com.nnm.bot.render.config;

import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.util.ObjUtil;

import com.sun.management.OperatingSystemMXBean;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.spi.RenderEngineProvider;
import com.nnm.bot.render.spi.RenderEngineProviderContext;
import com.nnm.bot.render.spi.RenderEngineProviderLoader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** 校验渲染配置并选择适用的渲染提供者；外部 JAR 通过 ServiceLoader 发现。 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RenderProperties.class, RenderPluginProperties.class})
public class RenderEngineConfig {

    /** 为 Chromium 保留足够内存余量；4GB 部署配置优先使用轻量渲染引擎。 */
    private static final long PLAYWRIGHT_MIN_PHYSICAL_MEMORY = 8L * 1024 * 1024 * 1024;

    /** 引擎与模板处理的运行参数。 */
    @Resource
    private RenderProperties properties;
    /** 外部插件发现开关与目录。 */
    @Resource
    private RenderPluginProperties pluginProperties;
    /** 负责发现、排序并管理提供者类加载器。 */
    @Resource
    private RenderEngineProviderLoader providerLoader;
    /** 初始化后可用的内置和外部引擎。 */
    private List<RenderEngineProvider> providers = List.of();
    /** 可选的显式引擎覆盖值。 */
    private String configuredProvider;

    /** 校验配置并发现可用提供者。 */
    @PostConstruct
    void initialize() {
        properties.validate();
        this.providers = providerLoader.load(pluginProperties);
        this.configuredProvider = pluginProperties.getProvider();
    }

    /** 创建按当前配置选定的渲染引擎。 */
    @Bean
    public RenderEngine renderEngine() {
        RenderEngineProviderContext context =
            new RenderEngineProviderContext("file:///templates/", properties.getPlaywright().getChromiumArgs(),
                properties.getFonts().getPaths().stream().map(Path::of).toList());
        if (CharSequenceUtil.isNotBlank(configuredProvider)) {
            return create(CharSequenceUtil.trim(configuredProvider), context);
        }
        return switch (properties.getEngine()) {
            case PLAYWRIGHT -> create("playwright", context);
            case OPENHTMLTOPDF -> create("openhtmltopdf", context);
            case AUTO -> autoSelect(context);
        };
    }

    /** 自动模式优先选择可用且符合内存预算的引擎。 */
    private RenderEngine autoSelect(RenderEngineProviderContext context) {
        boolean enoughMemoryForPlaywright = hasEnoughMemoryForPlaywright();
        for (RenderEngineProvider provider : providers) {
            if (CharSequenceUtil.equalsIgnoreCase(provider.id(), "playwright") && !enoughMemoryForPlaywright) {
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

    /**
     * 按标识创建明确指定的提供者。
     *
     * @throws IllegalStateException 提供者不存在或当前不可用时
     */
    private RenderEngine create(String id, RenderEngineProviderContext context) {
        RenderEngineProvider provider = find(id);
        if (ObjUtil.isNull(provider) || !provider.isAvailable(context)) {
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
        return providers.stream().filter(provider -> CharSequenceUtil.trim(provider.id()).equalsIgnoreCase(normalized))
            .findFirst().orElse(null);
    }

    /** Chromium 占用较高，自动选择时要求机器具备最低物理内存预算。 */
    private boolean hasEnoughMemoryForPlaywright() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            long totalPhysical = osBean.getTotalMemorySize();
            return totalPhysical >= PLAYWRIGHT_MIN_PHYSICAL_MEMORY;
        } catch (Throwable t) {
            log.warn("Unable to inspect physical memory; using a conservative JVM heap fallback: {}", t.getMessage());
            return Runtime.getRuntime().maxMemory() > 2L * 1024 * 1024 * 1024;
        }
    }
}
