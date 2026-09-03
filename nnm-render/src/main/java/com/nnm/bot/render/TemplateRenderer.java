package com.nnm.bot.render;

import java.util.Map;

public interface TemplateRenderer {

    /** 处理数据库中保存的 HTML 模板。 */
    String renderHtml(String html, Map<String, Object> variables);
}
