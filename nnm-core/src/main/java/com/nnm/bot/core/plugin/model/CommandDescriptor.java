package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import com.nnm.bot.core.constant.MatchType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 用于注册和展示机器人命令的不可变元数据。
 *
 * @param code 用于配置和权限校验的稳定命令编码
 * @param name 命令主名称
 * @param triggers 额外的命令触发词
 * @param description 帮助菜单中的说明
 * @param permission 可选的权限码
 * @param matchType 命令查找策略
 * @param order 展示顺序和回退匹配顺序
 */
public record CommandDescriptor(
        String code,
        String name,
        List<CommandTrigger> triggers,
        String description,
        String permission,
        MatchType matchType,
        int order
) {

    public CommandDescriptor {
        code = requireText(code, "code");
        name = requireText(name, "name");
        description = Objects.requireNonNullElse(description, "");
        permission = Objects.requireNonNullElse(permission, "");
        matchType = Objects.requireNonNull(matchType, "matchType must not be null");
        triggers = immutableTriggers(triggers);
    }

    public List<CommandTrigger> lookupTriggers() {
        Map<String, CommandTrigger> lookup = new LinkedHashMap<>();
        for (CommandTrigger trigger : triggers) {
            lookup.put(trigger.name().toLowerCase(Locale.ROOT), trigger);
        }
        lookup.putIfAbsent(
                name.toLowerCase(Locale.ROOT),
                new CommandTrigger(name, CommandBoundaryPolicy.defaultFor(name)));
        return List.copyOf(lookup.values());
    }

    private static List<CommandTrigger> immutableTriggers(List<CommandTrigger> triggers) {
        if (triggers == null || triggers.isEmpty()) {
            return List.of();
        }
        Map<String, CommandTrigger> normalized = new LinkedHashMap<>();
        for (CommandTrigger trigger : triggers) {
            CommandTrigger item = Objects.requireNonNull(trigger, "trigger must not be null");
            normalized.putIfAbsent(item.name().toLowerCase(Locale.ROOT), item);
        }
        return List.copyOf(normalized.values());
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
