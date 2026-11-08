package com.nnm.bot.core.plugin.api;

/** 模块强类型命令目录的单项定义，不包含运行时数据库配置。 */
public interface CommandSpecification {
    /** 返回稳定命令编码。 */
    String code();

    /** 返回默认中文显示名称。 */
    String displayName();

    /** 返回展示及前缀匹配优先级，数值越小越优先。 */
    int order();
}
