package com.nnm.bot.warframe.persistent.official.config.worldstate;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

/**
 * WorldStateDTO 代码簿配置。
 *
 * <p>代码簿属于应用运行所需的静态参考数据，不和动态 WorldStateDTO 快照混在一起。
 * 运行时唯一从数据库读取，数据库中的简体中文记录优先、英文记录兜底。</p>
 */
@ConfigurationProperties(prefix = "nnm.warframe.query.world-state")
public class WarframeWorldStateDataProperties {

    private String locale = "zh";
    /** 每个 JVM 校准已加载代码簿的间隔，漏掉广播后仍从数据库恢复；不拉取动态世界状态。 */
    private Duration codebookRefreshInterval = Duration.ofMinutes(5);

    /**
     * 读取每个 JVM 校准已加载代码簿的间隔，漏掉广播后仍从数据库恢复；不拉取动态世界状态。。
     *
     * @return 每个 JVM 校准已加载代码簿的间隔，漏掉广播后仍从数据库恢复；不拉取动态世界状态。。
     */
    public Duration getCodebookRefreshInterval() {
        return codebookRefreshInterval;
    }

    /**
     * 设置每个 JVM 校准已加载代码簿的间隔，漏掉广播后仍从数据库恢复；不拉取动态世界状态。。
     *
     * @param codebookRefreshInterval 代码簿刷新间隔。
     */
    public void setCodebookRefreshInterval(Duration codebookRefreshInterval) {
        this.codebookRefreshInterval = codebookRefreshInterval;
    }

    /**
     * 读取语言区域。
     *
     * @return 语言区域对应的文本。
     */
    public String getLocale() {
        return locale;
    }

    /**
     * 设置语言区域。
     *
     * @param locale 当前语言区域代码。
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (codebookRefreshInterval == null || codebookRefreshInterval.toMillis() <= 0) {
            throw new IllegalArgumentException(
                    "nnm.warframe.query.world-state.codebook-refresh-interval must be at least 1ms");
        }
        if (StrUtil.isBlank(locale)) {
            throw new IllegalArgumentException(
                    "nnm.warframe.query.world-state.locale must not be blank");
        }
        if (!"zh".equalsIgnoreCase(locale) && !"en".equalsIgnoreCase(locale)) {
            throw new IllegalArgumentException(
                    "nnm.warframe.query.world-state.locale must be zh or en");
        }
    }
}
