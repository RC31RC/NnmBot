package com.nnm.bot.core.plugin.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 数据库命令配置在核心层的只读投影。 */
public record CommandExecutionConfiguration(
        boolean enabled,
        boolean showInHelp,
        PresentationPolicy presentation,
        List<CommandTemplate> templates,
        List<CommandTrigger> triggers,
        String permissionOverride,
        long cooldownSeconds,
        boolean mentionInvoker) {

    public CommandExecutionConfiguration {
        presentation = ObjectUtil.defaultIfNull(presentation, PresentationPolicy.defaults());
        templates = CollUtil.isEmpty(templates) ? List.of() : List.copyOf(templates);
        triggers = immutableTriggers(triggers);
        permissionOverride = StrUtil.blankToDefault(permissionOverride, "").trim();
        cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public static CommandExecutionConfiguration defaults() {
        return new CommandExecutionConfiguration(true, true, PresentationPolicy.defaults(),
                List.of(), List.of(), "", 0, false);
    }

    public static CommandExecutionConfiguration disabledDefaults() {
        return new CommandExecutionConfiguration(false, false, PresentationPolicy.defaults(),
                List.of(), List.of(), "", 0, false);
    }

    public String templateHtml(String viewKey) {
        return templates.stream().filter(template -> template.viewKey().equals(viewKey))
                .map(CommandTemplate::html).filter(html -> html != null && !html.isBlank()).findFirst().orElse(null);
    }

    public CommandTemplate markdownTemplate(String viewKey) {
        return templates.stream().filter(template -> template.viewKey().equals(viewKey))
                .filter(template -> StrUtil.isNotBlank(template.markdown()) && StrUtil.isNotBlank(template.plainText()))
                .findFirst().orElse(null);
    }

    public record CommandTemplate(String code, String viewKey, String html, String markdown, String plainText) {
        public CommandTemplate {
            if (StrUtil.isBlank(code) || (StrUtil.isBlank(html) && StrUtil.isBlank(markdown))) {
                throw new IllegalArgumentException("Template code and at least one renderer must not be blank");
            }
            if (StrUtil.isNotBlank(markdown) && StrUtil.isBlank(plainText)) {
                throw new IllegalArgumentException("Markdown renderer requires an explicit plain text template");
            }
            code = code.trim();
            if (viewKey == null || !viewKey.matches("[a-z][a-z0-9_.-]{0,79}")) {
                throw new IllegalArgumentException("Invalid template view key");
            }
        }
    }

    private static List<CommandTrigger> immutableTriggers(List<CommandTrigger> source) {
        if (CollUtil.isEmpty(source)) return List.of();
        Map<String, CommandTrigger> unique = new LinkedHashMap<>();
        for (CommandTrigger trigger : source) {
            if (trigger != null) unique.putIfAbsent(trigger.name().toLowerCase(Locale.ROOT), trigger);
        }
        return List.copyOf(unique.values());
    }
}
