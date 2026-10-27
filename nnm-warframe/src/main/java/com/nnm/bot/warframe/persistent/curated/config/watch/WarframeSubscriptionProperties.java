package com.nnm.bot.warframe.persistent.curated.config.watch;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.List;

/** 订阅提前量、记忆窗口和每条规则的去重容量；不额外建立执行器。 */
@Getter
@Setter
@ConfigurationProperties("nnm.warframe.query.subscriptions")
public class WarframeSubscriptionProperties {
    private Duration advanceNotice = Duration.ofMinutes(10);
    private Duration matchRetention = Duration.ofDays(7);
    private int maxRememberedMatches = 2048;
    private List<String> specialInvasionRewards = List.of(
            "Forma", "OrokinCatalyst", "OrokinReactor", "Exilus",
            "福马", "奥罗金催化剂", "奥罗金反应堆", "特殊功能槽");

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (advanceNotice == null || advanceNotice.isNegative() || advanceNotice.isZero()
                || advanceNotice.compareTo(Duration.ofHours(1)) > 0
                || matchRetention == null || matchRetention.isNegative() || matchRetention.isZero()
                || matchRetention.compareTo(Duration.ofDays(30)) > 0
                || maxRememberedMatches < 1 || maxRememberedMatches > 10000
                || CollUtil.isEmpty(specialInvasionRewards)
                || specialInvasionRewards.stream().anyMatch(value -> StrUtil.isBlank(value))) {
            throw new IllegalArgumentException("Invalid nnm.warframe.query.subscriptions configuration");
        }
    }
}
