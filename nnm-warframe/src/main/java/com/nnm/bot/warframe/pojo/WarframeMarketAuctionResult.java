package com.nnm.bot.warframe.pojo;

import com.nnm.bot.common.response.PageResult;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/** 已筛选和分页的拍卖展示数据，不包含上游原始响应。 */
public record WarframeMarketAuctionResult(
        @JsonPropertyDescription("拍卖类型")
        String type,
        @JsonPropertyDescription("武器")
        String weapon,
        @JsonPropertyDescription("市场武器标识")
        String marketSlug,
        @JsonPropertyDescription("正面词条")
        List<String> positiveStats,
        @JsonPropertyDescription("负面词条")
        List<String> negativeStats,
        @JsonPropertyDescription("拍卖订单")
        PageResult<WarframeMarketAuctionView> auctions,
        @JsonPropertyDescription("必须有负面词条")
        boolean requireNegative) {}
