package com.nnm.bot.render;

import java.util.Map;

public interface ImageService {

    /** 将数据库 HTML 模板处理后转换为图片。 */
    byte[] generateHtml(String html, Map<String, Object> data);

    /** 将数据库 HTML 模板处理后按指定参数转换为图片。 */
    byte[] generateHtml(String html, Map<String, Object> data, RenderOptions options);
}
