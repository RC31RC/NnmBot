package com.nnm.bot.render.spi;

import java.util.List;
import java.nio.file.Path;

/** 外部渲染插件需要的最小、稳定上下文，不暴露 Spring 配置对象。 */
public record RenderEngineProviderContext(String templateBaseUri,
                                          List<String> chromiumArgs,
                                          List<Path> fontPaths) {

    public RenderEngineProviderContext(String templateBaseUri, List<String> chromiumArgs) {
        this(templateBaseUri, chromiumArgs, List.of());
    }

    public RenderEngineProviderContext {
        templateBaseUri = templateBaseUri == null ? "file:///templates/" : templateBaseUri;
        chromiumArgs = chromiumArgs == null ? List.of() : List.copyOf(chromiumArgs);
        fontPaths = fontPaths == null ? List.of() : List.copyOf(fontPaths);
    }
}
