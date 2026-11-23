package com.nnm.bot.core.plugin.api;

import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.collection.CollUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 数据库命令配置在核心层的只读投影。 */
public record CommandExecutionConfigurationDTO(boolean enabled, boolean showInHelp, CommandTemplateDTO template,
    CommandTemplateDTO functionDescriptionTemplate, List<CommandTrigger> triggers, String permissionOverride,
    long cooldownSeconds, boolean mentionInvoker) {

    public CommandExecutionConfigurationDTO {
        triggers = immutableTriggers(triggers);
        permissionOverride = CharSequenceUtil.trim(CharSequenceUtil.blankToDefault(permissionOverride, ""));
        cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public static CommandExecutionConfigurationDTO defaults() {
        return new CommandExecutionConfigurationDTO(true, true, null, null, List.of(), "", 0, false);
    }

    public static CommandExecutionConfigurationDTO disabledDefaults() {
        return new CommandExecutionConfigurationDTO(false, false, null, null, List.of(), "", 0, false);
    }

    /** 用功能说明模板替换本次 HTML 渲染模板，其余命令策略保持不变。 */
    public CommandExecutionConfigurationDTO withTemplate(CommandTemplateDTO selected) {
        return new CommandExecutionConfigurationDTO(enabled, showInHelp, selected, functionDescriptionTemplate,
            triggers, permissionOverride, cooldownSeconds, mentionInvoker);
    }

    public record CommandTemplateDTO(String code, String html) {
        public CommandTemplateDTO {
            if (CharSequenceUtil.isBlank(code) || CharSequenceUtil.isBlank(html))
                throw new IllegalArgumentException("HTML template code and content must not be blank");
            code = CharSequenceUtil.trim(code);
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
