package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.model.CommandCostConfiguration;

/**
 * 命令费用配置 SPI。配置来源可以是数据库，也可以由其他管理模块提供。
 */
public interface CommandCostConfigurationProvider {

    CommandCostConfiguration configuration(String commandCode);
}
