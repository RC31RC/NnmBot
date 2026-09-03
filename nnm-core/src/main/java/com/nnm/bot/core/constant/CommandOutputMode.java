package com.nnm.bot.core.constant;

/**
 * 命令的基础输出模式。渠道差异由响应层和渠道发送器处理。
 */
public enum CommandOutputMode {
    NONE,
    TEXT,
    IMAGE,
    TEXT_IMAGE,
    FILE,
    TEXT_FILE
}
