package com.nnm.bot.render.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "render")
public class RenderProperties {

    private Engine engine = Engine.AUTO;
    private final Playwright playwright = new Playwright();

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Playwright getPlaywright() {
        return playwright;
    }

    void validate() {
        if (engine == null) {
            throw new IllegalArgumentException("render.engine must be configured");
        }
        if (playwright.chromiumArgs == null) {
            throw new IllegalArgumentException(
                    "render.playwright.chromium-args must not be null");
        }
        if (playwright.chromiumArgs.stream().anyMatch(argument ->
                argument == null || argument.isBlank())) {
            throw new IllegalArgumentException(
                    "render.playwright.chromium-args must not contain blank values");
        }
    }

    public enum Engine {
        AUTO,
        OPENHTMLTOPDF,
        PLAYWRIGHT
    }

    public static class Playwright {

        private List<String> chromiumArgs = new ArrayList<>(
                List.of("--no-sandbox", "--disable-gpu"));

        public List<String> getChromiumArgs() {
            return chromiumArgs;
        }

        public void setChromiumArgs(List<String> chromiumArgs) {
            this.chromiumArgs = chromiumArgs;
        }
    }
}
