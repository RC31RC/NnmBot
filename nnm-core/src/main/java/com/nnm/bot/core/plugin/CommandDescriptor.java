package com.nnm.bot.core.plugin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable metadata used to register and display a bot command.
 *
 * @param code stable command code used by configuration and permissions
 * @param name primary display name
 * @param aliases additional exact-match names
 * @param description help text
 * @param permission optional permission code
 * @param matchType command lookup strategy
 * @param order display and fallback order
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
