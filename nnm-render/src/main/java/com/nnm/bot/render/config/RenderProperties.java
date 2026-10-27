package com.nnm.bot.render.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.render.constant.RenderEngineType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "render")
public class RenderProperties {

    private RenderEngineType engine = RenderEngineType.AUTO;
    private final Playwright playwright = new Playwright();
    private final Fonts fonts = new Fonts();
    private final Limits limits = new Limits();

    public Limits getLimits() {
        return limits;
    }

    public RenderEngineType getEngine() {
        return engine;
    }

    public void setEngine(RenderEngineType engine) {
        this.engine = engine;
    }

    public Playwright getPlaywright() {
        return playwright;
    }

    public Fonts getFonts() {
        return fonts;
    }

    void validate() {
        if (ObjectUtil.isNull(engine)) {
            throw new IllegalArgumentException("render.engine must be configured");
        }
        if (ObjectUtil.isNull(playwright.chromiumArgs)) {
            throw new IllegalArgumentException(
                    "render.playwright.chromium-args must not be null");
        }
        if (playwright.chromiumArgs.stream().anyMatch(argument ->
                StrUtil.isBlank(argument))) {
            throw new IllegalArgumentException(
                    "render.playwright.chromium-args must not contain blank values");
        }
        if (ObjectUtil.isNull(fonts.paths) || fonts.paths.stream().anyMatch(StrUtil::isBlank)) {
            throw new IllegalArgumentException(
                    "render.fonts.paths must not contain blank values");
        }
        if (limits.maxInputBytes < 1 || limits.maxImagePixels < 1 || limits.maxResultBytes < 1) {
            throw new IllegalArgumentException("render.limits must be positive");
        }
    }

    @Getter
    @Setter
    public static class Limits {
        private int maxInputBytes = 2 * 1024 * 1024;
        private long maxImagePixels = 8_000_000;
        private int maxResultBytes = 8 * 1024 * 1024;
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
