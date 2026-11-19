package com.nnm.bot.core.plugin.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 数据库命令配置在核心层的只读投影。 */
public record CommandExecutionConfigurationDTO(boolean enabled, boolean showInHelp, CommandTemplateDTO template,
    List<CommandTrigger> triggers, String permissionOverride, long cooldownSeconds, boolean mentionInvoker) {

    public CommandExecutionConfigurationDTO {
        triggers = immutableTriggers(triggers);
        permissionOverride = StrUtil.blankToDefault(permissionOverride, "").trim();
        cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public static CommandExecutionConfigurationDTO defaults() {
        return new CommandExecutionConfigurationDTO(true, true, null, List.of(), "", 0, false);
    }

    public static CommandExecutionConfigurationDTO disabledDefaults() {
        return new CommandExecutionConfigurationDTO(false, false, null, List.of(), "", 0, false);
    }

    public record CommandTemplateDTO(String code, String html) {
        public CommandTemplateDTO {
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
