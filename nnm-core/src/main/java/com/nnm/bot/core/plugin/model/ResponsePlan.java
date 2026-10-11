package com.nnm.bot.core.plugin.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.model.BotMarkdownMessage;

import java.util.Arrays;
import java.util.List;

/**
 * 命令响应的不可变执行计划。
 *
 * <p>计划由命令响应分发器生成，渠道策略只负责把它转换成平台消息；
 * 发送器不读取命令配置，也不决定输出顺序。</p>
 *
 * @param outputMode 本次响应的最终输出模式
 * @param text 可复制的文字结果
 * @param image 已渲染的图片内容
 * @param attachments 待发送文件
 * @param markdown 渠道策略需要发送的 Markdown 消息
 * @param mentionUserId 可选的平台用户标识；渠道不支持提及时应忽略
 */
public record ResponsePlan(ChannelOutputMode outputMode,
                           String text,
                           byte[] image,
                           List<CommandAttachment> attachments,
                           BotMarkdownMessage markdown,
                           String mentionUserId) {

    public ResponsePlan(ChannelOutputMode outputMode,
                        String text,
                        byte[] image,
                        List<CommandAttachment> attachments,
                        BotMarkdownMessage markdown) {
        this(outputMode, text, image, attachments, markdown, null);
    }

    public ResponsePlan {
        outputMode = ObjectUtil.isNull(outputMode) ? ChannelOutputMode.TEXT : outputMode;
        text = StrUtil.blankToDefault(text, "");
        image = image == null ? null : Arrays.copyOf(image, image.length);
        attachments = CollUtil.isEmpty(attachments) ? List.of() : List.copyOf(attachments);
        mentionUserId = StrUtil.isBlank(mentionUserId) ? null : mentionUserId.trim();
    }

    @Override
    public byte[] image() {
        return image == null ? null : Arrays.copyOf(image, image.length);
    }

    public boolean hasText() {
        return StrUtil.isNotBlank(text);
    }

    public boolean hasImage() {
        return image != null && image.length > 0;
    }

    public boolean hasAttachments() {
        return CollUtil.isNotEmpty(attachments);
    }

    public static ResponsePlan text(String text) {
        return new ResponsePlan(ChannelOutputMode.TEXT, text, null, List.of(), null, null);
    }
}
