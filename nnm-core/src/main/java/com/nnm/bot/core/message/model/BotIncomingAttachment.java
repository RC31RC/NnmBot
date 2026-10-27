package com.nnm.bot.core.message.model;

import com.nnm.bot.core.constant.BotMessageConstants.BotIncomingAttachmentType;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * 渠道无关的入站附件引用。
 *
 * <p>这里只保存平台事件提供的元数据和远程引用，不在消息接收阶段下载文件。
 * 后续 OCR 或文件处理服务应根据 url/fileId 进行受控暂存和校验。</p>
 *
 * @param type 附件类型
 * @param fileName 平台提供的文件名或资源名
 * @param contentType MIME 类型，未知时为空
 * @param url 平台提供的下载地址
 * @param fileId 平台侧文件标识
 * @param size 平台提供的文件大小
 * @param width 图片宽度
 * @param height 图片高度
 * @param metadata 平台扩展元数据
 */
public record BotIncomingAttachment(
        BotIncomingAttachmentType type,
        String fileName,
        String contentType,
        String url,
        String fileId,
        Long size,
        Integer width,
        Integer height,
        Map<String, String> metadata) {

    public BotIncomingAttachment {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        fileName = StrUtil.blankToDefault(fileName, "attachment");
        contentType = StrUtil.blankToDefault(contentType, "application/octet-stream");
        url = StrUtil.blankToDefault(url, null);
        fileId = StrUtil.blankToDefault(fileId, null);
        size = size != null && size >= 0 ? size : null;
        width = width != null && width > 0 ? width : null;
        height = height != null && height > 0 ? height : null;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean hasRemoteReference() {
        return StrUtil.isNotBlank(url) || StrUtil.isNotBlank(fileId);
    }

    public boolean isImage() {
        return type == BotIncomingAttachmentType.IMAGE;
    }
}
