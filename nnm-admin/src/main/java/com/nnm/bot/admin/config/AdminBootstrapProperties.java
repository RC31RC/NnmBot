package com.nnm.bot.admin.config;

import cn.hutool.core.util.StrUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nnm.admin.bootstrap")
public class AdminBootstrapProperties {

    private boolean enabled;
    private String username = "admin";
    private String password = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void validateWhenEnabled() {
        if (!enabled) {
            return;
        }
        if (StrUtil.isBlank(username)) {
            throw new IllegalStateException(
                    "nnm.admin.bootstrap.username must be configured");
        }
        if (StrUtil.isBlank(password)) {
            throw new IllegalStateException(
                    "nnm.admin.bootstrap.password must be configured");
        }
    }
}
