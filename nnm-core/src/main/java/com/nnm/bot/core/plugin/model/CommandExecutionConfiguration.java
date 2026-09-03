package com.nnm.bot.core.plugin.model;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.constant.CommandOutputMode;
import com.nnm.bot.core.constant.BotPlatform;
import cn.hutool.core.util.StrUtil;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 数据库命令配置在核心层的只读投影。
 *
 * @param enabled 是否启用命令
 * @param showInHelp 是否显示在帮助菜单
 * @param outputMode 默认输出方式
 * @param templates 命令关联的 HTML 模板，按管理端排序，第一项为默认模板
 * @param triggers 数据库配置的命令触发词
 * @param permissionOverride 覆盖插件默认权限的权限码
 * @param cooldownSeconds 冷却秒数
 * @param mentionInvoker 是否在支持的平台回复中提及命令触发者
 * @param channelOutputModes 按渠道覆盖的输出方式
 */
public record CommandExecutionConfiguration(
        boolean enabled,
        boolean showInHelp,
        CommandOutputMode outputMode,
        List<CommandTemplate> templates,
        List<CommandTrigger> triggers,
        String permissionOverride,
        long cooldownSeconds,
        boolean mentionInvoker,
        Map<BotPlatform, ChannelOutputMode> channelOutputModes
) {

    public CommandExecutionConfiguration(
            boolean enabled,
            boolean showInHelp,
            CommandOutputMode outputMode,
            List<CommandTemplate> templates,
            List<CommandTrigger> triggers,
            String permissionOverride,
            long cooldownSeconds,
            Map<BotPlatform, ChannelOutputMode> channelOutputModes) {
        this(enabled, showInHelp, outputMode, templates, triggers, permissionOverride,
                cooldownSeconds, false, channelOutputModes);
    }

    public CommandExecutionConfiguration {
        outputMode = ObjectUtil.isNull(outputMode) ? CommandOutputMode.TEXT : outputMode;
        templates = CollUtil.isEmpty(templates)
                ? List.of()
                : templates.stream()
                        .filter(ObjectUtil::isNotNull)
                        .map(template -> new CommandTemplate(
                                template.code(), template.html()))
                        .filter(template -> ObjectUtil.isNotNull(template))
                        .toList();
        triggers = immutableTriggers(triggers);
        permissionOverride = ObjectUtil.isNull(permissionOverride) ? "" : permissionOverride.trim();
        cooldownSeconds = Math.max(0, cooldownSeconds);
        EnumMap<BotPlatform, ChannelOutputMode> modes = new EnumMap<>(BotPlatform.class);
        if (MapUtil.isNotEmpty(channelOutputModes)) {
            channelOutputModes.forEach((platform, mode) -> {
                if (ObjectUtil.isNotNull(platform) && ObjectUtil.isNotNull(mode)) {
                    modes.put(platform, mode);
                }
            });
        }
        channelOutputModes = Collections.unmodifiableMap(modes);
    }

    public static CommandExecutionConfiguration defaults() {
        return new CommandExecutionConfiguration(
                true, true, CommandOutputMode.TEXT, List.of(), List.of(), "", 0, false, Map.of());
    }

    /**
     * 插件存在但尚未由管理端落库时使用的安全默认值。
     */
    public static CommandExecutionConfiguration disabledDefaults() {
        return new CommandExecutionConfiguration(
                false, false, CommandOutputMode.TEXT, List.of(), List.of(), "", 0, false, Map.of());
    }

    public ChannelOutputMode outputMode(BotPlatform platform) {
        return channelOutputModes.getOrDefault(platform, ChannelOutputMode.from(outputMode));
    }

    /** 返回当前命令的默认模板编码，兼容图片渲染和缓存调用方。 */
    public String templateCode() {
        return templates.isEmpty() ? null : templates.get(0).code();
    }

    /** 返回当前命令的默认模板内容，兼容图片渲染和缓存调用方。 */
    public String templateHtml() {
        return templates.isEmpty() ? null : templates.get(0).html();
    }

    public record CommandTemplate(String code, String html) {
        public CommandTemplate {
            code = StrUtil.isBlank(code) ? null : code.trim();
            html = StrUtil.isBlank(html) ? null : html;
        }
    }

    private static List<CommandTrigger> immutableTriggers(List<CommandTrigger> triggers) {
        if (CollUtil.isEmpty(triggers)) {
            return List.of();
        }
        Map<String, CommandTrigger> unique = new LinkedHashMap<>();
        for (CommandTrigger trigger : triggers) {
            if (ObjectUtil.isNotNull(trigger)) {
                unique.putIfAbsent(trigger.name().toLowerCase(Locale.ROOT), trigger);
            }
        }
        return List.copyOf(unique.values());
    }
}
