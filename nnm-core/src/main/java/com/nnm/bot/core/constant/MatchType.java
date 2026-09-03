package com.nnm.bot.core.constant;

/**
 * 描述插件处理消息前采用的命令匹配方式。
 */
public enum MatchType {
    /**
     * 规范化后的消息必须等于命令名称或其某个别名。
     */
    EXACT,

    /**
     * 消息以命令名称或别名开头，剩余内容作为命令参数。
     */
    PREFIX
}
