package com.nnm.bot.warframe.runtime.config.market;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;

/** Warframe Market v1/v2 接口配置。 */
@ConfigurationProperties(prefix = "nnm.warframe.pull.market-api")
public class WarframeMarketApiProperties {

    /** v2 是当前物品目录和订单的主协议，v1 仅保留给仍未迁移的接口。 */
    private String version = "v2";
    private URI v1BaseUrl = URI.create("https://api.warframe.market/v1");
    private URI v2BaseUrl = URI.create("https://api.warframe.market/v2");
    private String language = "zh-hans";
    private String platform = "pc";
    private String apiKey = "";
    private String userAgent = "NnmBot/1.0";
    /** WM 普通订单每页条数；拍卖查询保持独立的分页规则。 */
    private int orderPageSize = 5;
    /** 每个统计时段按金币/铂金加权比值保留的推荐数量。 */
    private int ducatsLimit = 10;

    /**
     * 读取每个统计时段按金币/铂金加权比值保留的推荐数量。。
     *
     * @return 每个统计时段按金币/铂金加权比值保留的推荐数量。。
     */
    public int getDucatsLimit() { return ducatsLimit; }

    /**
     * 设置每个统计时段按金币/铂金加权比值保留的推荐数量。。
     *
     * @param ducatsLimit 杜卡德金币上限。
     */
    public void setDucatsLimit(int ducatsLimit) { this.ducatsLimit = ducatsLimit; }

    /**
     * 读取WM 普通订单每页条数；拍卖查询保持独立的分页规则。。
     *
     * @return WM 普通订单每页条数；拍卖查询保持独立的分页规则。。
     */
    public int getOrderPageSize() {
        return orderPageSize;
    }

    /**
     * 设置WM 普通订单每页条数；拍卖查询保持独立的分页规则。。
     *
     * @param orderPageSize 订单分页数量。
     */
    public void setOrderPageSize(int orderPageSize) {
        this.orderPageSize = orderPageSize;
    }

    /**
     * 读取v2 是当前物品目录和订单的主协议，v1 仅保留给仍未迁移的接口。。
     *
     * @return v2 是当前物品目录和订单的主协议，v1 仅保留给仍未迁移的接口。。
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置v2 是当前物品目录和订单的主协议，v1 仅保留给仍未迁移的接口。。
     *
     * @param version 本次操作已经确定的数据版本。
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 读取基础地址。
     *
     * @return 地址对象。
     */
    public URI getV1BaseUrl() {
        return v1BaseUrl;
    }

    /**
     * 设置基础地址。
     *
     * @param v1BaseUrl 值基础地址。
     */
    public void setV1BaseUrl(URI v1BaseUrl) {
        this.v1BaseUrl = v1BaseUrl;
    }

    /**
     * 读取基础地址。
     *
     * @return 地址对象。
     */
    public URI getV2BaseUrl() {
        return v2BaseUrl;
    }

    /**
     * 设置基础地址。
     *
     * @param v2BaseUrl 值基础地址。
     */
    public void setV2BaseUrl(URI v2BaseUrl) {
        this.v2BaseUrl = v2BaseUrl;
    }

    /**
     * 读取语言。
     *
     * @return 语言对应的文本。
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 设置语言。
     *
     * @param language 本地化语言代码。
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 读取平台。
     *
     * @return 平台对应的文本。
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置平台。
     *
     * @param platform 市场或官方接口使用的平台代码。
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 读取接口键。
     *
     * @return 接口键对应的文本。
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 设置接口键。
     *
     * @param apiKey 接口键。
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 读取用户代理人物。
     *
     * @return 用户代理人物对应的文本。
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置用户代理人物。
     *
     * @param userAgent 用户代理人物。
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (ducatsLimit < 1 || ducatsLimit > 50) {
            throw new IllegalArgumentException("nnm.warframe.pull.market-api.ducats-limit must be between 1 and 50");
        }
        if (orderPageSize < 1 || orderPageSize > 10) {
            throw new IllegalArgumentException("nnm.warframe.pull.market-api.order-page-size must be between 1 and 10");
        }
        requireHttpUrl(v1BaseUrl, "nnm.warframe.pull.market-api.v1-base-url");
        requireHttpUrl(v2BaseUrl, "nnm.warframe.pull.market-api.v2-base-url");
        if (!"v1".equalsIgnoreCase(version) && !"v2".equalsIgnoreCase(version)) {
            throw new IllegalArgumentException(
                    "nnm.warframe.pull.market-api.version must be v1 or v2");
        }
        requireText(language, "nnm.warframe.pull.market-api.language");
        requireText(platform, "nnm.warframe.pull.market-api.platform");
        requireText(userAgent, "nnm.warframe.pull.market-api.user-agent");
    }

    /**
     * 校验市场接口地址使用允许的 HTTP 协议并包含有效主机。
     * @param value 待处理的字段值。
     * @param name 当前业务对象的名称。
     */
    private static void requireHttpUrl(URI value, String name) {
        if (value == null || value.getHost() == null
                || (!"http".equalsIgnoreCase(value.getScheme())
                && !"https".equalsIgnoreCase(value.getScheme()))) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL");
        }
    }

    /**
     * 校验并读取必需的非空白文本。
     *
     * @param value 待校验或规范化的文本。
     * @param name 当前业务对象的名称。
     */
    private static void requireText(String value, String name) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
