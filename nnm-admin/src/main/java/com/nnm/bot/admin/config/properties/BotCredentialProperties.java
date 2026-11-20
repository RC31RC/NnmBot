package com.nnm.bot.admin.config.properties;

import cn.hutool.core.text.CharSequenceUtil;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 提供渠道凭据加密所需的应用级密钥。 */
@Component
@ConfigurationProperties(prefix = "nnm.bot.credentials")
public class BotCredentialProperties {

    /** 加密渠道访问凭据的密钥；启动时要求至少 16 个字符。 */
    private String encryptionKey;

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * 在创建凭据加密组件前验证密钥配置。
     *
     * @throws IllegalArgumentException 密钥为空或长度不足
     */
    @PostConstruct
    void validate() {
        if (CharSequenceUtil.isBlank(encryptionKey) || encryptionKey.length() < 16) {
            throw new IllegalArgumentException(
                "nnm.bot.credentials.encryption-key must contain at least 16 characters");
        }
    }
}
