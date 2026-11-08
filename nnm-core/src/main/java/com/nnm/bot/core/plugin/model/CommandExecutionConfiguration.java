package com.nnm.bot.core.plugin.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 数据库命令配置在核心层的只读投影。 */
public record CommandExecutionConfiguration(boolean enabled, boolean showInHelp, CommandTemplate template,
    List<CommandTrigger> triggers, String permissionOverride, long cooldownSeconds, boolean mentionInvoker) {

    public CommandExecutionConfiguration {
        triggers = immutableTriggers(triggers);
        permissionOverride = StrUtil.blankToDefault(permissionOverride, "").trim();
        cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public static CommandExecutionConfiguration defaults() {
        return new CommandExecutionConfiguration(true, true, null, List.of(), "", 0, false);
    }

    public static CommandExecutionConfiguration disabledDefaults() {
        return new CommandExecutionConfiguration(false, false, null, List.of(), "", 0, false);
    }

    public record CommandTemplate(String code, String html) {
        public CommandTemplate {
            if (StrUtil.isBlank(code) || StrUtil.isBlank(html))
                throw new IllegalArgumentException("HTML template code and content must not be blank");
            code = code.trim();
        }
    }

    private static List<CommandTrigger> immutableTriggers(List<CommandTrigger> source) {
        if (CollUtil.isEmpty(source))
            return List.of();
        Map<String, CommandTrigger> unique = new LinkedHashMap<>();
        for (CommandTrigger trigger : source) {
            if (trigger != null)
                unique.putIfAbsent(trigger.name().toLowerCase(Locale.ROOT), trigger);
        }
        return List.copyOf(unique.values());
    }
}
