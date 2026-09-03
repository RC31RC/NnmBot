package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.model.CommandDefinition;

import java.util.Map;

/**
 * 命令配置读取 SPI。核心模块不依赖管理模块和数据库实现。
 */
public interface CommandConfigurationProvider {

    /**
     * 批量读取全部命令定义。调用方会将结果构建为运行时快照。
     */
    Map<String, CommandDefinition> loadAll();
}
