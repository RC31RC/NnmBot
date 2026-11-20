package com.nnm.bot.core.plugin.api;

import cn.hutool.core.text.CharSequenceUtil;

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
public record CommandDefinitionDTO(String code, String name, String gameCode, String description,
    CommandExecutionConfigurationDTO configuration) {

    public CommandDefinitionDTO {
        code = requireText(code, "code");
        name = requireText(name, "name");
        gameCode = requireText(gameCode, "gameCode");
        description = CharSequenceUtil.trim(Objects.requireNonNullElse(description, ""));
        configuration = Objects.requireNonNull(configuration, "configuration must not be null");
    }

    private static String requireText(String value, String field) {
        String normalized = CharSequenceUtil.trim(Objects.requireNonNull(value, field + " must not be null"));
        if (CharSequenceUtil.isEmpty(normalized)) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
