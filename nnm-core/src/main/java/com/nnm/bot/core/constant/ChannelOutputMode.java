package com.nnm.bot.core.constant;

import cn.hutool.core.util.ObjectUtil;

/**
 * 渠道专属输出模式，包含 QQ 官方 Markdown 等协议能力。
 */
public enum ChannelOutputMode {
    /**
     * 仅发送 CommandResult.text，不发送 data、图片或文件附件。
     */
    NONE,
    TEXT,
    IMAGE,
    FILE,
    MARKDOWN;

    public static ChannelOutputMode from(CommandOutputMode mode) {
        return ObjectUtil.isNull(mode) ? TEXT : ChannelOutputMode.valueOf(mode.name());
    }
}
