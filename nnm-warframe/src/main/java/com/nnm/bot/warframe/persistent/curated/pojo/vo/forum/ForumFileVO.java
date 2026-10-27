package com.nnm.bot.warframe.persistent.curated.pojo.vo.forum;

/**
 * 论坛富文本附件上传结果。
 */
public record ForumFileVO(
        String url,
        String alt,
        String href,
        String name,
        String contentType,
        long size
) {
}
