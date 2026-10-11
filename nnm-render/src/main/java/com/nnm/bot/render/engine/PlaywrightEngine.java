package com.nnm.bot.render.engine;

import com.microsoft.playwright.options.ScreenshotType;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PlaywrightEngine implements RenderEngine {

    private final List<String> chromiumArgs;
    private Object browser;

    public PlaywrightEngine(List<String> chromiumArgs) {
        this.chromiumArgs = chromiumArgs;
    }

    @Override
    public byte[] renderHtmlToImage(String html) {
        return renderHtmlToImage(html, RenderOptions.builder().build());
    }

    @Override
    public byte[] renderHtmlToImage(String html, RenderOptions options) {
        try {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(chromiumArgs)
            );
            Page page = browser.newPage();
            page.setViewportSize(options.getWidth(), 800);
            page.setContent(html);
            page.waitForLoadState();

            byte[] screenshot = page.screenshot(
                    new Page.ScreenshotOptions()
                            .setFullPage(true)
                            .setType(ScreenshotType.PNG)
            );

            page.close();
            browser.close();
            playwright.close();

            return screenshot;
        } catch (Exception e) {
            log.error("Playwright render failed", e);
            throw new RuntimeException("Image render failed", e);
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
