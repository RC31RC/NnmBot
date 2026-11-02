package com.nnm.bot.warframe.runtime.config.worldstate;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nnm.warframe.pull.polling")
public class WarframePollingProperties {

    /** 是否注册世界状态轮询；默认暂停，手工管理端刷新不受影响。 */
    private boolean enabled = false;

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
}
