package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;

import java.util.Objects;

/**
 * 一个可匹配的命令触发词及其参数边界策略。
 */
public record CommandTrigger(
        String name,
        CommandBoundaryPolicy boundaryPolicy
) {

    public CommandTrigger {
        name = Objects.requireNonNull(name, "name must not be null").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        boundaryPolicy = Objects.requireNonNullElse(
                boundaryPolicy, CommandBoundaryPolicy.defaultFor(name));
    }
}
