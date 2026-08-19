package com.nnm.bot.core.plugin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用于注册和展示机器人命令的不可变元数据。
 *
 * @param code 用于配置和权限校验的稳定命令编码
 * @param name 命令主名称
 * @param aliases 额外的精确匹配名称
 * @param description 帮助菜单中的说明
 * @param permission 可选的权限码
 * @param matchType 命令查找策略
 * @param order 展示顺序和回退匹配顺序
 */
public record CommandDescriptor(
        String code,
        String name,
        Set<String> aliases,
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
        aliases = immutableAliases(aliases);
    }

    public Set<String> lookupNames() {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        names.add(name);
        names.addAll(aliases);
        return Collections.unmodifiableSet(names);
    }

    private static Set<String> immutableAliases(Set<String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return Set.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String alias : aliases) {
            normalized.add(requireText(alias, "alias"));
        }
        return Collections.unmodifiableSet(normalized);
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
