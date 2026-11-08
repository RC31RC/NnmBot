package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.MatchType;

/** 模块目录与插件注解合并后的稳定命令元数据。 */
public record CommandMetadata(String code, String name, String gameCode, MatchType matchType, int order) {
}
