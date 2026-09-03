package com.nnm.bot.core.constant;

import cn.hutool.core.util.ObjectUtil;

/**
 * 渠道专属输出模式，包含 QQ 官方 Markdown 等协议能力。
 */
public enum ChannelOutputMode {
    NONE,
    TEXT,
    IMAGE,
    TEXT_IMAGE,
    FILE,
    TEXT_FILE,
    MARKDOWN;

    public static ChannelOutputMode from(CommandOutputMode mode) {
        return switch (ObjectUtil.isNull(mode) ? CommandOutputMode.TEXT : mode) {
            case NONE -> NONE;
            case TEXT -> TEXT;
            case IMAGE -> IMAGE;
            case TEXT_IMAGE -> TEXT_IMAGE;
            case FILE -> FILE;
            case TEXT_FILE -> TEXT_FILE;
        };
    }
}
