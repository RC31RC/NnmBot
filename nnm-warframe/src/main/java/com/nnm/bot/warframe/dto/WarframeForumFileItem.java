package com.nnm.bot.warframe.dto;

/**
 * 论坛富文本附件上传结果。
 */
public record WarframeForumFileItem(
        String url,
        String alt,
        String href,
        String name,
        String contentType,
        long size
) {
}
