package com.nnm.bot.core.plugin;

/**
 * 描述插件处理消息前采用的命令匹配方式。
 */
public enum MatchType {
    /**
     * 规范化后的消息必须等于命令名称或其某个别名。
     */
    EXACT,

    /**
     * 插件保留在有序的回退链中，由插件自行决定匹配规则。
     */
    CUSTOM
}
