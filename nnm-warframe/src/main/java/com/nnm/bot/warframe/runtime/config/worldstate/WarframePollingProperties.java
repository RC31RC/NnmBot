package com.nnm.bot.warframe.runtime.config.worldstate;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** 各实例世界状态拉取周期；任务始终注册，不再配置启用开关。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.polling")
public class WarframePollingProperties {
    private Duration interval = Duration.ofMinutes(2);

    /** 拒绝零或负数周期，防止错误配置造成持续请求。 */
    @PostConstruct
    public void validate() {
        if (interval == null || interval.isZero() || interval.isNegative()) {
            throw new SystemException(WarframeWorldStateErrorCode.POLLING_CONFIGURATION_INVALID,
                "nnm.warframe.pull.polling.interval 必须为正数");
        }
    }
}
