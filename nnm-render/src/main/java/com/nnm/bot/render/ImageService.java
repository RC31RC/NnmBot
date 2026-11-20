package com.nnm.bot.render;

import java.util.Map;

/** 将 HTML 模板与变量转换为图片的应用服务。 */
public interface ImageService {

    /**
     * 使用默认参数渲染 HTML。
     *
     * @param html HTML 模板
     * @param data 模板变量
     * @return PNG 图片字节
     */
    byte[] generateHtml(String html, Map<String, Object> data);

    /**
     * 按指定参数渲染 HTML。
     *
     * @param html HTML 模板
     * @param data 模板变量
     * @param options 图片尺寸与输出选项
     * @return 图片字节
     */
    byte[] generateHtml(String html, Map<String, Object> data, RenderOptions options);
}
