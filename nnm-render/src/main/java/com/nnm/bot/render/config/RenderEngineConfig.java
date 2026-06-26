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
        PlaywrightEngine playwright = createPlaywright();
        if (playwright.isAvailable() && isHighMemoryEnvironment()) {
            log.info("Auto-selected Playwright render engine");
            return playwright;
        }
        log.info("Auto-selected OpenHtmlToPdf render engine");
        return createOpenHtmlToPdf();
    }

    private OpenHtmlToPdfEngine createOpenHtmlToPdf() {
        return new OpenHtmlToPdfEngine("file:///templates/");
    }

    private PlaywrightEngine createPlaywright() {
        List<String> args = Arrays.asList(chromiumArgs.split(","));
        return new PlaywrightEngine(args);
    }

    private boolean isHighMemoryEnvironment() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return maxMemory > 2L * 1024 * 1024 * 1024;
    }
}
