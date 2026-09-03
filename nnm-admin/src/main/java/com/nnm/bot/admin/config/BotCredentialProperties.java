package com.nnm.bot.admin.config;

import cn.hutool.core.util.StrUtil;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnm.bot.credentials")
public class BotCredentialProperties {

    private String encryptionKey;

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @PostConstruct
    void validate() {
        if (StrUtil.isBlank(encryptionKey) || encryptionKey.length() < 16) {
            throw new IllegalArgumentException(
                    "nnm.bot.credentials.encryption-key must contain at least 16 characters");
        }
    }
}
