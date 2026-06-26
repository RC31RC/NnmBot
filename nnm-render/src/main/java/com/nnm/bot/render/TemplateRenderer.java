package com.nnm.bot.render;

import java.util.Map;

public interface TemplateRenderer {

    String render(String templateName, Map<String, Object> variables);
}
