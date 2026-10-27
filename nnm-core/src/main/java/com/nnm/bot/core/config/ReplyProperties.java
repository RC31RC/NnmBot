package com.nnm.bot.core.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.reply")
public class ReplyProperties {
    private int maxLogicalMessages = 8;
    private int maxBlocks = 32;
    private int maxTextCharacters = 32768;
    private int maxActions = 10;
    private int maxPlatformMessages = 16;
    private long maxImageBytes = 8L * 1024 * 1024;
    private long maxImagePixels = 20_000_000;
    private long maxFileBytes = 200L * 1024 * 1024;

    @PostConstruct
    void validate() {
        if (maxLogicalMessages < 1 || maxBlocks < 1 || maxTextCharacters < 1
                || maxActions < 1 || maxPlatformMessages < 1 || maxImageBytes < 1
                || maxImagePixels < 1 || maxFileBytes < 1) {
            throw new IllegalArgumentException("nnm.reply limits must be positive");
        }
    }
}
