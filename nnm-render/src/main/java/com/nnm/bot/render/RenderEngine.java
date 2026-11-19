package com.nnm.bot.render;

/**
 * 图片渲染引擎的 SPI 接口。 实现可以使用 openhtmltopdf、Playwright 或自定义渲染器。
 */
public interface RenderEngine {

    /** 使用引擎默认参数渲染 HTML。
     *
     * @param html 已完成模板处理的 HTML
     * @return 图片字节
     */
    byte[] renderHtmlToImage(String html);

    /** 将 HTML 渲染为图片；超出像素上限时实现应拒绝渲染。
     *
     * @param html 已完成模板处理的 HTML
     * @param options 输出参数及资源限制
     * @return 图片字节
     */
    byte[] renderHtmlToImage(String html, RenderOptions options);

    /** 返回可用于配置和日志识别的引擎标识。 */
    String engineName();

    /** 当前进程是否具备运行此引擎的依赖与环境。 */
    boolean isAvailable();
}
