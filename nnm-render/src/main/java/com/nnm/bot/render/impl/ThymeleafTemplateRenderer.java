package com.nnm.bot.render.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.render.TemplateRenderer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ThymeleafTemplateRenderer implements TemplateRenderer {

    private static final Pattern VARIABLE = Pattern.compile(
            "\\$\\{\\s*([A-Za-z_][A-Za-z0-9_]*)");
    private static final int MAX_INLINE_TEMPLATE_LENGTH = 256 * 1024;

    @Resource
    private SpringTemplateEngine templateEngine;

    @PostConstruct
    void initialize() {
        this.templateEngine.setTemplateResolver(inlineTemplateResolver());
    }

    @Override
    public String renderHtml(String html, Map<String, Object> variables) {
        if (StrUtil.isBlank(html)) {
            throw new IllegalArgumentException("inline html must not be blank");
        }
        Map<String, Object> safeVariables = MapUtil.isEmpty(variables) ? Map.of() : variables;
        validateInlineTemplate(html, safeVariables);
        Context context = new Context();
        context.setVariables(safeVariables);
        return this.templateEngine.process(html, context);
    }

    private static void validateInlineTemplate(String html, Map<String, Object> variables) {
        if (html.length() > MAX_INLINE_TEMPLATE_LENGTH) {
            throw new IllegalArgumentException("inline html template is too large");
        }
        String lowerCase = html.toLowerCase(java.util.Locale.ROOT);
        if (lowerCase.contains("<script")
                || lowerCase.contains("th:utext")
                || lowerCase.contains("@{")
                || lowerCase.contains("t(")
                || lowerCase.contains("__")) {
            throw new IllegalArgumentException("inline html contains unsupported expression");
        }
        Matcher matcher = VARIABLE.matcher(html);
        while (matcher.find()) {
            if (!variables.containsKey(matcher.group(1))) {
                throw new IllegalArgumentException(
                        "inline html references an unavailable variable: " + matcher.group(1));
            }
        }
    }

    private static StringTemplateResolver inlineTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setName("nnmbotInlineTemplateResolver");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        return resolver;
    }
}
