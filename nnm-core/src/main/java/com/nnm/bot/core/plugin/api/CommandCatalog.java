package com.nnm.bot.core.plugin.api;

import java.util.Map;

/** 模块目录的 Spring 注册契约；稳定元数据集中在所属模块 constant 包。 */
public interface CommandCatalog {
    /** 返回本目录所属的业务域编码。 */
    String gameCode();

    /** 返回以命令编码索引的不可变强类型目录。 */
    Map<String, ? extends CommandSpecification> entries();
}
