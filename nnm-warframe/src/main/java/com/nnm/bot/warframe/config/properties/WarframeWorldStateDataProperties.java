package com.nnm.bot.warframe.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WorldState 代码簿配置。
 *
 * <p>代码簿属于应用运行所需的静态参考数据，不和动态 WorldState 快照混在一起。
 * 运行时唯一从数据库读取，数据库中的简体中文记录优先、英文记录兜底。</p>
 */
@ConfigurationProperties(prefix = "nnm.warframe.worldstate-data")
public class WarframeWorldStateDataProperties {

    private String locale = "zh";

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void validate() {
        if (locale == null || locale.isBlank()) {
            throw new IllegalArgumentException(
                    "nnm.warframe.worldstate-data.locale must not be blank");
        }
        if (!"zh".equalsIgnoreCase(locale) && !"en".equalsIgnoreCase(locale)) {
            throw new IllegalArgumentException(
                    "nnm.warframe.worldstate-data.locale must be zh or en");
        }
    }
}
