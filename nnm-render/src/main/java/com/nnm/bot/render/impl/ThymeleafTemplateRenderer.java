package com.nnm.bot.render.impl;

import com.nnm.bot.render.TemplateRenderer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class ThymeleafTemplateRenderer implements TemplateRenderer {

    @Resource
    private TemplateEngine templateEngine;

    @Override
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
