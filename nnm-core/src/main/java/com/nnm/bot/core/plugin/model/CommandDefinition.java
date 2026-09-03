package com.nnm.bot.core.plugin.model;

import java.util.Objects;

/**
 * 数据库命令定义在核心层的只读投影。
 *
 * @param code 稳定命令编码
 * @param name 命令显示名称
 * @param gameCode 所属功能域编码
 * @param description 命令说明
 * @param configuration 命令运行配置
 */
public record CommandDefinition(
        String code,
        String name,
        String gameCode,
        String description,
        CommandExecutionConfiguration configuration
) {

    public CommandDefinition {
        code = requireText(code, "code");
        name = requireText(name, "name");
        gameCode = requireText(gameCode, "gameCode");
        description = Objects.requireNonNullElse(description, "").trim();
        configuration = Objects.requireNonNull(
                configuration, "configuration must not be null");
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(
                value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
