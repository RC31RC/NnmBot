package com.nnm.bot.render.constant;

/** 渲染引擎选择策略。 */
public enum RenderEngineType {
    /** 按可用性与运行资源自动选择。 */
    AUTO,
    /** 使用 OpenHTMLtoPDF 渲染。 */
    OPENHTMLTOPDF,
    /** 使用无头 Chromium 渲染。 */
    PLAYWRIGHT
}
