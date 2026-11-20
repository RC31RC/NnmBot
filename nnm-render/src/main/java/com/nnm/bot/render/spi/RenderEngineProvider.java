package com.nnm.bot.render.spi;

import com.nnm.bot.render.RenderEngine;

/**
 * NnmBot 渲染引擎 SPI。实现可以随应用发布，也可以通过 plugin 目录中的 JAR 使用 {@code META-INF/services/...RenderEngineProvider} 注册。
 */
public interface RenderEngineProvider {

    /** 返回全局唯一的提供者标识，用于配置选择和去重。 */
    String id();

    /** 数值越小越优先，仅用于 AUTO 模式的稳定排序。 */
    default int order() {
        return 100;
    }

    /**
     * 检查当前运行环境是否满足引擎启动条件。
     *
     * @param context 引擎运行上下文
     * @return 满足条件时为 {@code true}
     */
    boolean isAvailable(RenderEngineProviderContext context);

    /**
     * 按运行上下文创建引擎实例。
     *
     * @param context 引擎运行上下文
     * @return 可执行 HTML 图片渲染的引擎
     */
    RenderEngine create(RenderEngineProviderContext context);
}
