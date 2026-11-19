package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.constant.MatchType;

/** 模块目录与插件注解合并后的稳定命令元数据。 */
public record CommandMetadataDTO(String code, String name, String gameCode, MatchType matchType, int order) {
}
