package com.nnm.bot.core.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 单条回复的逻辑消息、内容块、图片和文件资源上限。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.reply")
public class ReplyProperties {
    /** 单次命令允许产生的逻辑消息数。 */
    private int maxLogicalMessages = 8;
    /** 单次命令回复允许包含的内容块总数。 */
    private int maxBlocks = 32;
    /** 所有文本块的字符总量上限。 */
    private int maxTextCharacters = 32768;
    /** 单条回复允许的交互动作数。 */
    private int maxActions = 10;
    /** 按平台能力拆分后的消息数上限。 */
    private int maxPlatformMessages = 16;
    /** 单张图片最大编码字节数。 */
    private long maxImageBytes = 8L * 1024 * 1024;
    /** 单张图片最大像素数。 */
    private long maxImagePixels = 20_000_000;
    /** 单个文件最大字节数。 */
    private long maxFileBytes = 200L * 1024 * 1024;

    /** 启动时拒绝非正资源上限。
     *
     * @throws IllegalArgumentException 任一上限小于 1
     */
    @PostConstruct
    void validate() {
        if (maxLogicalMessages < 1 || maxBlocks < 1 || maxTextCharacters < 1 || maxActions < 1
            || maxPlatformMessages < 1 || maxImageBytes < 1 || maxImagePixels < 1 || maxFileBytes < 1) {
            throw new IllegalArgumentException("nnm.reply limits must be positive");
        }
    }
}
