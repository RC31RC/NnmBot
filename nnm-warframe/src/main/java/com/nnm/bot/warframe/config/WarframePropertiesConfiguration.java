package com.nnm.bot.warframe.config;

import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationProperties;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationPullProperties;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.config.cache.WarframeCacheProperties;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumProperties;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumStorageProperties;
import com.nnm.bot.warframe.persistent.official.config.image.WarframeImageDataProperties;
import com.nnm.bot.warframe.runtime.config.market.WarframeMarketApiProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeOfficialApiProperties;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.runtime.config.riven.WarframeRivenWeeklyProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeBountyCycleProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeCycleProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframePollingProperties;
import com.nnm.bot.warframe.persistent.curated.config.watch.WarframeSubscriptionProperties;
import com.nnm.bot.warframe.persistent.official.config.worldstate.WarframeWorldStateDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 各业务根配置独立绑定前缀，嵌套容量和生命周期仍由所属业务统一管理。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
public class WarframePropertiesConfiguration {
    /**
     * 注册社区配置，前缀由属性类声明。
     * @return 游戏资料社区配置对象。
     */
    @Bean
    public WarframeCommunityProperties warframeCommunityProperties() {
        return new WarframeCommunityProperties();
    }

    /**
     * 注册论坛配置，前缀由属性类声明。
     * @return 论坛配置对象。
     */
    @Bean
    public ForumProperties warframeForumProperties() {
        return new ForumProperties();
    }

    /**
     * 注册论坛文件存储子配置，与论坛根配置共享同一绑定结果。
     * @param properties 当前业务配置。
     * @return 论坛存储配置对象。
     */
    @Bean
    public ForumStorageProperties warframeForumStorageProperties(ForumProperties properties) {
        return properties.getStorage();
    }

    /**
     * 注册官方实时接口配置，前缀由属性类声明。
     * @return 官方接口配置对象。
     */
    @Bean
    public WarframeOfficialApiProperties warframeOfficialApiProperties() {
        return new WarframeOfficialApiProperties();
    }

    /**
     * 注册官方数据集配置，前缀由属性类声明。
     * @return 官方数据集配置对象。
     */
    @Bean
    public WarframeOfficialImportProperties warframeOfficialImportProperties() {
        return new WarframeOfficialImportProperties();
    }

    /**
     * 注册市场接口配置，前缀由属性类声明。
     * @return 市场接口配置对象。
     */
    @Bean
    public WarframeMarketApiProperties warframeMarketApiProperties() {
        return new WarframeMarketApiProperties();
    }

    /**
     * 注册世界状态轮询配置，前缀由属性类声明。
     * @return 世界状态轮询配置对象。
     */
    @Bean
    public WarframePollingProperties warframePollingProperties() {
        return new WarframePollingProperties();
    }

    /**
     * 注册赏金轮换配置，前缀由属性类声明。
     * @return 赏金轮换配置对象。
     */
    @Bean
    public WarframeBountyCycleProperties warframeBountyCycleProperties() {
        return new WarframeBountyCycleProperties();
    }

    /**
     * 注册紫卡每周数据配置，前缀由属性类声明。
     * @return 紫卡每周数据配置对象。
     */
    @Bean
    public WarframeRivenWeeklyProperties warframeRivenWeeklyProperties() {
        return new WarframeRivenWeeklyProperties();
    }

    /**
     * 注册图片数据配置，前缀由属性类声明。
     * @return 图片数据配置对象。
     */
    @Bean
    public WarframeImageDataProperties warframeImageDataProperties() {
        return new WarframeImageDataProperties();
    }

    /**
     * 注册订阅查询配置，前缀由属性类声明。
     * @return 订阅查询配置对象。
     */
    @Bean
    public WarframeSubscriptionProperties warframeSubscriptionProperties() {
        return new WarframeSubscriptionProperties();
    }

    /**
     * 注册世界状态代码簿配置，前缀由属性类声明。
     * @return 世界状态代码簿配置对象。
     */
    @Bean
    public WarframeWorldStateDataProperties warframeWorldStateDataProperties() {
        return new WarframeWorldStateDataProperties();
    }

    /**
     * 注册周期计算配置，前缀由属性类声明。
     * @return 周期计算配置对象。
     */
    @Bean
    public WarframeCycleProperties warframeCycleProperties() {
        return new WarframeCycleProperties();
    }

    /**
     * 注册仲裁查询配置，前缀由属性类声明。
     * @return 仲裁查询配置对象。
     */
    @Bean
    public WarframeArbitrationProperties warframeArbitrationProperties() {
        return new WarframeArbitrationProperties();
    }

    /**
     * 注册仲裁来源拉取配置，前缀由属性类声明。
     * @return 仲裁来源拉取配置对象。
     */
    @Bean
    public WarframeArbitrationPullProperties warframeArbitrationPullProperties() {
        return new WarframeArbitrationPullProperties();
    }

    /**
     * 注册缓存容量和生命周期配置，前缀由属性类声明。
     * @return 缓存配置对象。
     */
    @Bean
    public WarframeCacheProperties warframeCacheProperties() {
        return new WarframeCacheProperties();
    }
}
