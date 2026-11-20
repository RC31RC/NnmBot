package com.nnm.bot.render.config;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.render.constant.RenderEngineType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/** 渲染引擎及资源上限配置。 */
@ConfigurationProperties(prefix = "render")
public class RenderProperties {

    /** 默认渲染引擎选择策略。 */
    private RenderEngineType engine = RenderEngineType.AUTO;
    /** Playwright 浏览器启动选项。 */
    private final Playwright playwright = new Playwright();
    /** 可用于 CJK 字符的字体文件。 */
    private final Fonts fonts = new Fonts();
    /** 输入、输出与像素数量限制。 */
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

    /**
     * 校验配置值，避免无效限制在首次渲染时才暴露。
     *
     * @throws IllegalArgumentException 配置缺失或限制不为正数时
     */
    void validate() {
        if (ObjUtil.isNull(engine)) {
            throw new IllegalArgumentException("render.engine must be configured");
        }
        if (ObjUtil.isNull(playwright.chromiumArgs)) {
            throw new IllegalArgumentException("render.playwright.chromium-args must not be null");
        }
        if (playwright.chromiumArgs.stream().anyMatch(argument -> CharSequenceUtil.isBlank(argument))) {
            throw new IllegalArgumentException("render.playwright.chromium-args must not contain blank values");
        }
        if (ObjUtil.isNull(fonts.paths) || fonts.paths.stream().anyMatch(CharSequenceUtil::isBlank)) {
            throw new IllegalArgumentException("render.fonts.paths must not contain blank values");
        }
        if (limits.maxInputBytes < 1 || limits.maxImagePixels < 1 || limits.maxResultBytes < 1) {
            throw new IllegalArgumentException("render.limits must be positive");
        }
    }

    /** 单次渲染允许消耗的输入与输出资源上限。 */
    @Getter
    @Setter
    public static class Limits {
        /** 模板及变量序列化结果的最大字节数。 */
        private int maxInputBytes = 2 * 1024 * 1024;
        /** 输出位图的最大像素数。 */
        private long maxImagePixels = 8_000_000;
        /** 最终图片的最大字节数。 */
        private int maxResultBytes = 8 * 1024 * 1024;
    }

    /** Chromium 启动参数。 */
    public static class Playwright {

        /** 传给 Chromium 的命令行参数。 */
        private List<String> chromiumArgs = new ArrayList<>(List.of("--no-sandbox", "--disable-gpu"));

        public List<String> getChromiumArgs() {
            return chromiumArgs;
        }

        public void setChromiumArgs(List<String> chromiumArgs) {
            this.chromiumArgs = chromiumArgs;
        }
    }

    /** 系统中可选的字体文件路径。 */
    public static class Fonts {

        /** 按平台提供的候选字体路径。 */
        private List<String> paths = defaultPaths();

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }

        /** 返回跨 Windows、macOS 与 Linux 的常见 CJK 字体候选路径。 */
        private static List<String> defaultPaths() {
            String windowsDirectory = System.getenv().getOrDefault("WINDIR", "C:/Windows");
            return new ArrayList<>(
                List.of(windowsDirectory + "/Fonts/simhei.ttf", windowsDirectory + "/Fonts/simsunb.ttf",
                    windowsDirectory + "/Fonts/NotoSansSC-VF.ttf", windowsDirectory + "/Fonts/msyh.ttc",
                    "/System/Library/Fonts/PingFang.ttc", "/System/Library/Fonts/STHeiti Light.ttc",
                    "/Library/Fonts/Arial Unicode.ttf", "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
                    "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc",
                    "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc"));
        }
    }
}
