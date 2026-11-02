package com.nnm.bot.render.impl;

import java.util.Locale;
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
import java.util.regex.Pattern;

@Component
public class ThymeleafTemplateRenderer implements TemplateRenderer {

    private static final int MAX_INLINE_TEMPLATE_LENGTH = 256 * 1024;
    private static final Pattern TYPE_EXPRESSION = Pattern.compile("(?i)(?<![a-z0-9_])t\\s*\\(");

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
        validateInlineTemplate(html);
        Context context = new Context();
        context.setVariables(safeVariables);
        return this.templateEngine.process(html, context);
    }

    private static void validateInlineTemplate(String html) {
        if (html.length() > MAX_INLINE_TEMPLATE_LENGTH) {
            throw new IllegalArgumentException("inline html template is too large");
        }
        String lowerCase = html.toLowerCase(Locale.ROOT);
        if (lowerCase.contains("<script") || lowerCase.contains("th:utext") || lowerCase.contains("@{")
            || TYPE_EXPRESSION.matcher(html).find() || lowerCase.contains("__")) {
            throw new IllegalArgumentException("inline html contains unsupported expression");
        }
        // 由 Thymeleaf 解析 th:each/th:with 的局部作用域和缺失的可选值。
        // 不能仅按入参键检查表达式根，否则会拒绝合法的局部变量模板。
    }

    private static StringTemplateResolver inlineTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setName("nnmbotInlineTemplateResolver");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        return resolver;
    }
}
