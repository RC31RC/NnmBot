package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

/**
 * wf配装命令的候选文章展示对象。
 *
 * <p>标题、分类和 URL 是文字、图片及 Markdown 渠道共用的数据字段，不返回文章正文。</p>
 */
@CommandTextInline
public record WarframeForumArticleLinkDisplay(
        @JsonPropertyDescription("标题")
        String title,
        @JsonPropertyDescription("分类")
        String category,
        @JsonPropertyDescription("链接")
        String url
) {
}
