package com.nnm.bot.core.channel.topology;

/** 数据库适配器提供一次完整读取，核心模块不依赖管理端实体。 */
public interface BotTopologyLoader {
    BotTopologySnapshot load();
}
