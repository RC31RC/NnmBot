package com.nnm.bot.render.config;

import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.engine.OpenHtmlToPdfEngine;
import com.nnm.bot.render.engine.PlaywrightEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class RenderEngineConfig {

    /**
     * 启用 Playwright 所需的系统物理内存下限（字节）。
     * Playwright 会启动独立的 Chromium 进程，消耗的是 JVM 堆之外的系统内存，
     * 因此用系统物理总内存而非 JVM 堆上限来判断，避免在小内存机器（如 4G）上误启浏览器导致 OOM。
     */
    private static final long PLAYWRIGHT_MIN_PHYSICAL_MEMORY = 4L * 1024 * 1024 * 1024;

    @Value("${render.engine:auto}")
    private String engineType;

    @Value("${render.openhtmltopdf.font-dir:classpath:fonts/}")
    private String fontDir;

    @Value("${render.playwright.chromium-args:--no-sandbox,--disable-gpu}")
    private String chromiumArgs;

    @Bean
    public RenderEngine renderEngine() {
        return switch (engineType) {
            case "playwright" -> createPlaywright();
            case "openhtmltopdf" -> createOpenHtmlToPdf();
            default -> autoSelect();
        };
    }

    private RenderEngine autoSelect() {
        if (!hasEnoughMemoryForPlaywright()) {
            log.info("系统物理内存不足 {}GB，自动选择 OpenHtmlToPdf 渲染引擎（省内存）",
                    PLAYWRIGHT_MIN_PHYSICAL_MEMORY / 1024 / 1024 / 1024);
            return createOpenHtmlToPdf();
        }
        PlaywrightEngine playwright = createPlaywright();
        if (playwright.isAvailable()) {
            log.info("Auto-selected Playwright render engine");
            return playwright;
        }
        log.info("Playwright 不可用（依赖或浏览器驱动缺失），降级为 OpenHtmlToPdf 渲染引擎");
        return createOpenHtmlToPdf();
    }

    private OpenHtmlToPdfEngine createOpenHtmlToPdf() {
        return new OpenHtmlToPdfEngine("file:///templates/");
    }

    private PlaywrightEngine createPlaywright() {
        List<String> args = Arrays.asList(chromiumArgs.split(","));
        return new PlaywrightEngine(args);
    }

    /**
     * 判断系统物理内存是否足以启动 Playwright（Chromium）。
     * 优先读取系统物理总内存；无法获取时回退到 JVM 堆上限作为保守估计。
     */
    private boolean hasEnoughMemoryForPlaywright() {
        try {
            var osBean = (com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            long totalPhysical = osBean.getTotalMemorySize();
            log.debug("系统物理总内存: {} MB", totalPhysical / 1024 / 1024);
            return totalPhysical >= PLAYWRIGHT_MIN_PHYSICAL_MEMORY;
        } catch (Throwable t) {
            // 无法获取物理内存时，回退到 JVM 堆上限判断（保守：堆需 > 2G 才认为环境足够大）
            log.warn("无法获取系统物理内存，回退到 JVM 堆判断: {}", t.getMessage());
            return Runtime.getRuntime().maxMemory() > 2L * 1024 * 1024 * 1024;
        }
    }
}
