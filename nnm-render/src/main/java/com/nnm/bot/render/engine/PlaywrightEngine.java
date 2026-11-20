package com.nnm.bot.render.engine;

import com.microsoft.playwright.options.ScreenshotType;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.common.constant.ComputationErrorCode;
import com.nnm.bot.common.error.BusinessException;

import java.util.List;

/** 使用无头 Chromium 渲染支持浏览器布局的 HTML 页面。 */
public class PlaywrightEngine implements RenderEngine {

    /** 启动 Chromium 时附加的命令行参数。 */
    private final List<String> chromiumArgs;

    /**
     * 创建使用指定 Chromium 参数的引擎。
     *
     * @param chromiumArgs 浏览器启动参数
     */
    public PlaywrightEngine(List<String> chromiumArgs) {
        this.chromiumArgs = chromiumArgs;
    }

    @Override
    public byte[] renderHtmlToImage(String html) {
        return renderHtmlToImage(html, RenderOptions.builder().build());
    }

    @Override
    public byte[] renderHtmlToImage(String html, RenderOptions options) {
        // 浏览器与页面按调用创建并关闭，避免并发渲染共享页面状态。
        try (Playwright playwright = Playwright.create(); Browser browser =
            playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setArgs(chromiumArgs))) {
            Page page = browser.newPage();
            page.setViewportSize(options.getWidth(), 800);
            page.setContent(html);
            page.waitForLoadState();

            Number width = (Number)page.evaluate("Math.max(innerWidth, document.documentElement.scrollWidth)");
            Number height = (Number)page.evaluate("Math.max(innerHeight, document.documentElement.scrollHeight)");
            if (width.doubleValue() * height.doubleValue() > options.getMaxImagePixels()) {
                throw new BusinessException(ComputationErrorCode.RESULT_TOO_LARGE);
            }

            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true).setType(ScreenshotType.PNG));
        }
    }

    @Override
    public String engineName() {
        return "playwright";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.microsoft.playwright.Playwright");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
