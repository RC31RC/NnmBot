package com.nnm.bot.warframe.persistent.official.pojo.vo.reference;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

/** Wiki 与配装文章的可访问链接，不将正文或管理端字段送入消息。 */
@CommandTextInline
public record WarframeLinkVO(
        @JsonPropertyDescription("名称")
        String name,
        @JsonPropertyDescription("链接")
        String url) {}
