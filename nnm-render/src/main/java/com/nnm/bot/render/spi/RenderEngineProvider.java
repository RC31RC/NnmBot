package com.nnm.bot.render.spi;

import com.nnm.bot.render.RenderEngine;

/**
 * NnmBot 渲染引擎 SPI。实现可以随应用发布，也可以通过 plugin 目录中的 JAR
 * 使用 {@code META-INF/services/...RenderEngineProvider} 注册。
 */
public interface RenderEngineProvider {

    String id();

    /** 数值越小越优先，仅用于 AUTO 模式的稳定排序。 */
    default int order() {
        return 100;
    }

    boolean isAvailable(RenderEngineProviderContext context);

    RenderEngine create(RenderEngineProviderContext context);
}
