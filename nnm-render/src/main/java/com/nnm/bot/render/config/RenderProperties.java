package com.nnm.bot.render.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "render")
public class RenderProperties {

    private Engine engine = Engine.AUTO;
    private final Playwright playwright = new Playwright();
    private final Fonts fonts = new Fonts();

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Playwright getPlaywright() {
        return playwright;
    }

    public Fonts getFonts() {
        return fonts;
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
        if (fonts.paths == null || fonts.paths.stream().anyMatch(path ->
                path == null || path.isBlank())) {
            throw new IllegalArgumentException(
                    "render.fonts.paths must not contain blank values");
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

    public static class Fonts {

        private List<String> paths = defaultPaths();

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }

        private static List<String> defaultPaths() {
            String windowsDirectory = System.getenv().getOrDefault("WINDIR", "C:/Windows");
            return new ArrayList<>(List.of(
                    windowsDirectory + "/Fonts/simhei.ttf",
                    windowsDirectory + "/Fonts/simsunb.ttf",
                    windowsDirectory + "/Fonts/NotoSansSC-VF.ttf",
                    windowsDirectory + "/Fonts/msyh.ttc",
                    "/System/Library/Fonts/PingFang.ttc",
                    "/System/Library/Fonts/STHeiti Light.ttc",
                    "/Library/Fonts/Arial Unicode.ttf",
                    "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
                    "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc",
                    "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc"
            ));
        }
    }
}
