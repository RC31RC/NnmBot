package com.nnm.bot.render.spi;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.nio.file.Path;

/**
 * 外部渲染插件需要的最小、稳定上下文，不暴露 Spring 配置对象。
 *
 * @param templateBaseUri HTML 中相对资源的基础地址，不是业务模板来源
 * @param chromiumArgs Chromium 启动参数
 * @param fontPaths 可用字体文件路径
 */
public record RenderEngineProviderContext(String templateBaseUri,
                                          List<String> chromiumArgs,
                                          List<Path> fontPaths) {

    public RenderEngineProviderContext(String templateBaseUri, List<String> chromiumArgs) {
        this(templateBaseUri, chromiumArgs, List.of());
    }

    public RenderEngineProviderContext {
        templateBaseUri = ObjectUtil.isNull(templateBaseUri) ? "file:///templates/" : templateBaseUri;
        chromiumArgs = CollUtil.isEmpty(chromiumArgs) ? List.of() : List.copyOf(chromiumArgs);
        fontPaths = CollUtil.isEmpty(fontPaths) ? List.of() : List.copyOf(fontPaths);
    }
}
