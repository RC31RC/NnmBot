package com.nnm.bot.admin.security;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.config.BotCredentialProperties;
import com.nnm.bot.common.exception.SystemException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** 对持久化的机器人连接凭据执行带认证的加密。 */
@Component
public class BotCredentialCipher {

    private static final String PREFIX = "enc:v1:";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    @Resource
    private BotCredentialProperties properties;
    private SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    @PostConstruct
    void initialize() {
        this.key = new SecretKeySpec(
                sha256(this.properties.getEncryptionKey()),
                "AES");
    }

    public String encrypt(String plaintext) {
        if (StrUtil.isBlank(plaintext)) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException exception) {
            throw new SystemException("机器人连接凭据加密失败", exception);
        }
    }

    public String decrypt(String ciphertext) {
        if (StrUtil.isBlank(ciphertext)) {
            return null;
        }
        if (!ciphertext.startsWith(PREFIX)) {
            throw new SystemException("机器人连接凭据不是受支持的密文格式");
        }
        try {
            byte[] payload = Base64.getDecoder().decode(ciphertext.substring(PREFIX.length()));
            if (payload.length <= IV_LENGTH) {
                throw new GeneralSecurityException("encrypted payload is too short");
            }
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | GeneralSecurityException exception) {
            throw new SystemException("机器人连接凭据解密失败", exception);
        }
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
