package com.nnm.bot.render;

/**
 * SPI interface for image rendering engines.
 * Implementations can use openhtmltopdf, Playwright, or any custom renderer.
 */
public interface RenderEngine {

    byte[] renderHtmlToImage(String html);

    byte[] renderHtmlToImage(String html, RenderOptions options);

    String engineName();

    boolean isAvailable();
}
