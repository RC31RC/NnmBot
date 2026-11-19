package com.nnm.bot.render.config;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/** 内置与外部渲染引擎提供者的发现配置。 */
@ConfigurationProperties(prefix = "render.plugins")
public class RenderPluginProperties {

    /** 是否启用渲染提供者发现。 */
    private boolean enabled = true;
    /** 外部 JAR 会在当前进程内执行代码，因此必须显式开启发现。 */
    private boolean externalEnabled;
    /** 外部提供者 JAR 所在目录。 */
    private Path directory = Path.of("./plugin");
    /** 指定提供者标识；为空时使用渲染策略自动选择。 */
    private String provider;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExternalEnabled() {
        return externalEnabled;
    }

    public void setExternalEnabled(boolean externalEnabled) {
        this.externalEnabled = externalEnabled;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    /** 校验插件目录配置。
     *
     * @throws IllegalArgumentException 插件目录为空时
     */
    public void validate() {
        if (ObjectUtil.isNull(directory)) {
            throw new IllegalArgumentException("render.plugins.directory must not be null");
        }
    }
}
