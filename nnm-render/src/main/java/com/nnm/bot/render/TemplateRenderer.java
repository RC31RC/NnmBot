package com.nnm.bot.render;

import java.util.Map;

/** 在调用具体渲染引擎前处理 HTML 模板。 */
public interface TemplateRenderer {

    /**
     * 将变量应用到 HTML 模板。
     *
     * @param html 模板文本
     * @param variables 模板变量
     * @return 处理后的 HTML
     */
    String renderHtml(String html, Map<String, Object> variables);
}
