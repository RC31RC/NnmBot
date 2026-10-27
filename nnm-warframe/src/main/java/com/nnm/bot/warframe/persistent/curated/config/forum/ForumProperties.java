package com.nnm.bot.warframe.persistent.curated.config.forum;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import java.net.URI;

@ConfigurationProperties(prefix = "nnm.warframe.forum")
public class ForumProperties {

    /** 附件策略属于论坛业务；对象存储连接仍由 nnm.storage 管理。 */
    @NestedConfigurationProperty
    private final ForumStorageProperties storage = new ForumStorageProperties();

    /**
     * 读取附件策略属于论坛业务；对象存储连接仍由 nnm.storage 管理。。
     *
     * @return 附件策略属于论坛业务；对象存储连接仍由 nnm.storage 管理。。
     */
    public ForumStorageProperties getStorage() {
        return storage;
    }

    private URI publicBaseUrl = URI.create("http://localhost:3006");

    private String publicDetailPath = "/#/warframe/forum/detail";

    /**
     * 读取公开基础地址。
     *
     * @return 地址对象。
     */
    public URI getPublicBaseUrl() {
        return publicBaseUrl;
    }

    /**
     * 设置公开基础地址。
     *
     * @param publicBaseUrl 公开基础地址。
     */
    public void setPublicBaseUrl(URI publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * 读取公开详情路径。
     *
     * @return 公开详情路径对应的文本。
     */
    public String getPublicDetailPath() {
        return publicDetailPath;
    }

    /**
     * 设置公开详情路径。
     *
     * @param publicDetailPath 公开详情路径。
     */
    public void setPublicDetailPath(String publicDetailPath) {
        this.publicDetailPath = publicDetailPath;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        storage.validate();
        if (publicBaseUrl == null
                || publicBaseUrl.getHost() == null
                || (!"http".equalsIgnoreCase(publicBaseUrl.getScheme())
                && !"https".equalsIgnoreCase(publicBaseUrl.getScheme()))) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.public-base-url must be an absolute HTTP URL");
        }
        if (StrUtil.isBlank(publicDetailPath)
                || !publicDetailPath.startsWith("/#/warframe/forum/detail")) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.public-detail-path must be a hash route under "
                            + "/#/warframe/forum/detail");
        }
    }

    /**
     * 构造指定文章的公开访问地址，便于机器人消息引用。
     * @param articleId 文章标识。
     * @return 公开文章地址对应的文本。
     */
    public String publicArticleUrl(long articleId) {
        String base = publicBaseUrl.toString().replaceAll("/+$", "");
        String path = publicDetailPath.replaceAll("/+$", "");
        return base + path + "/" + articleId;
    }

    /**
     * 构造指定文章的站内访问路径。
     * @param articleId 文章标识。
     * @return 公开文章地址对应的文本。
     */
    public String publicArticleUri(long articleId) {
        return publicDetailPath.replaceAll("/+$", "") + "/" + articleId;
    }
}
