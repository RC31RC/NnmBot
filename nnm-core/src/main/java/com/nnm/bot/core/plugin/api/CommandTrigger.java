package com.nnm.bot.core.plugin.api;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.core.plugin.constant.CommandBoundaryPolicy;

import java.util.Objects;

/**
 * 一个可匹配的命令触发词及其参数边界策略。
 */
public record CommandTrigger(String name, CommandBoundaryPolicy boundaryPolicy) {

    public CommandTrigger {
        name = CharSequenceUtil.trim(Objects.requireNonNull(name, "name must not be null"));
        if (CharSequenceUtil.isEmpty(name)) {
            throw new IllegalArgumentException("name must not be blank");
        }
        boundaryPolicy = Objects.requireNonNullElse(boundaryPolicy, CommandBoundaryPolicy.defaultFor(name));
    }
}
