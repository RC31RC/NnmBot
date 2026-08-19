package com.nnm.bot.render;

/**
 * 图片渲染引擎的 SPI 接口。
 * 实现可以使用 openhtmltopdf、Playwright 或自定义渲染器。
 */
public interface RenderEngine {

    byte[] renderHtmlToImage(String html);

    byte[] renderHtmlToImage(String html, RenderOptions options);

    String engineName();

    boolean isAvailable();
}
