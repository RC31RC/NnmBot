package com.nnm.bot.render.spi;

import java.util.List;

/** 外部渲染插件需要的最小、稳定上下文，不暴露 Spring 配置对象。 */
public record RenderEngineProviderContext(String templateBaseUri, List<String> chromiumArgs) {
    public RenderEngineProviderContext {
        templateBaseUri = templateBaseUri == null ? "file:///templates/" : templateBaseUri;
        chromiumArgs = chromiumArgs == null ? List.of() : List.copyOf(chromiumArgs);
    }
}
