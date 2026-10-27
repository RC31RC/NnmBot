package com.nnm.bot.warframe.runtime.config.worldstate;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;
import java.time.ZoneId;

@ConfigurationProperties(prefix = "nnm.warframe.pull.polling")
public class WarframePollingProperties {

    /** 是否注册世界状态轮询；默认暂停，手工管理端刷新不受影响。 */
    private boolean enabled = false;
    private String cron = "0 */2 * * * *";
    private ZoneId zone = ZoneId.of("Asia/Shanghai");

    /**
     * 读取是否注册世界状态轮询；默认暂停，手工管理端刷新不受影响。。
     *
     * @return 是否注册世界状态轮询；默认暂停，手工管理端刷新不受影响。。
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否注册世界状态轮询；默认暂停，手工管理端刷新不受影响。。
     *
     * @param enabled 操作后的启用状态。
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取调度表达式。
     *
     * @return 调度表达式对应的文本。
     */
    public String getCron() {
        return cron;
    }

    /**
     * 设置调度表达式。
     *
     * @param cron 调度表达式。
     */
    public void setCron(String cron) {
        this.cron = cron;
    }

    /**
     * 读取时区。
     *
     * @return 时区标识对象。
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * 设置时区。
     *
     * @param zone 时区。
     */
    public void setZone(ZoneId zone) {
        this.zone = zone;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (StrUtil.isBlank(cron) || !CronExpression.isValidExpression(cron)
                || zone == null) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.polling configuration");
        }
    }
}
