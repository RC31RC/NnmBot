package com.nnm.bot.core.plugin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

/** 积分查询与签到结算共用的展示对象，不暴露平台内部身份标识。 */
@CommandTextInline
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PointCommandDisplay(
        @JsonPropertyDescription("当前积分")
        long points,
        @JsonPropertyDescription("累计获得")
        Long totalEarned,
        @JsonPropertyDescription("累计消耗")
        Long totalSpent) {}
