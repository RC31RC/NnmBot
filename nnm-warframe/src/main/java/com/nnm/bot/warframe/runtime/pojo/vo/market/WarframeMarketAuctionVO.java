package com.nnm.bot.warframe.runtime.pojo.vo.market;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionPageDTO;
import com.nnm.bot.common.response.PageResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionEntryDTO.AuctionItemDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** 拍卖命令只展示报价、玩家和武器规格，不重复返回目录标识及完整接口对象。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeMarketAuctionVO(
        @JsonPropertyDescription("武器")
        String weapon,
        @JsonPropertyDescription("分页")
        String pagination,
        @JsonPropertyDescription("筛选")
        String filter,
        @JsonPropertyDescription("拍卖")
        List<AuctionVO> auctions,
        @JsonPropertyDescription("提示")
        String notice) {

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param result 当前正在组装的业务结果。
     * @param attributeName 属性名称。
     * @return 市场拍卖查询的分页展示结果。
     */
    public static WarframeMarketAuctionVO from(
            WarframeMarketAuctionPageDTO result, Function<String, String> attributeName) {
        PageResult<WarframeMarketAuctionEntryDTO> page = result.auctions();
        List<String> filters = new ArrayList<>();
        result.positiveStats().forEach(stat -> filters.add("正面 " + attributeName.apply(stat)));
        result.negativeStats().forEach(stat -> filters.add("负面 " + attributeName.apply(stat)));
        if (result.requireNegative()) filters.add("必须有负面词条");
        return new WarframeMarketAuctionVO(result.weapon(),
                page.current() + "/" + Math.max(1, (int) Math.ceil((double) page.total()
                        / Math.max(1, page.size()))) + " 页，共 " + page.total() + " 单",
                String.join("，", filters),
                page.records().stream().map(auction -> AuctionVO.from(auction, attributeName)).toList(),
                CollUtil.isEmpty(page.records()) ? "当前页没有符合条件的拍卖" : null);
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record AuctionVO(
            @JsonPropertyDescription("玩家")
            String userName,
            @JsonPropertyDescription("状态")
            String status,
            @JsonPropertyDescription("起拍")
            String startingPrice,
            @JsonPropertyDescription("一口价")
            String buyoutPrice,
            @JsonPropertyDescription("最高出价")
            String topBid,
            @JsonPropertyDescription("名称")
            String name,
            @JsonPropertyDescription("规格")
            String specifications,
            @JsonPropertyDescription("词条")
            List<String> attributes) {

        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param auction 拍卖。
         * @param attributeName 属性名称。
         * @return 单个市场拍卖的展示内容。
         */
        private static AuctionVO from(WarframeMarketAuctionEntryDTO auction,
                                    Function<String, String> attributeName) {
            AuctionItemDTO item = auction.item();
            List<String> specs = new ArrayList<>();
            List<String> stats = new ArrayList<>();
            if (item != null) {
                if (item.modRank() != null) specs.add("等级 " + item.modRank());
                if (item.reRolls() != null) specs.add("洗练 " + item.reRolls());
                if (item.masteryLevel() != null) specs.add("段位 " + item.masteryLevel());
                if (StrUtil.isNotBlank(item.polarity())) specs.add("极性 " + item.polarity());
                if (StrUtil.isNotBlank(item.element())) specs.add("元素 " + item.element());
                if (item.damage() != null) specs.add("加成 " + item.damage() + "%");
                if (item.havingEphemera() != null) specs.add(item.havingEphemera() ? "有幻纹" : "无幻纹");
                if (StrUtil.isNotBlank(item.quirk())) specs.add("怪癖 " + item.quirk());
                item.attributes().forEach(attribute -> stats.add(
                        (attribute.positive() ? "正面 " : "负面 ") + attributeName.apply(attribute.urlName())
                                + " " + attribute.value()));
            }
            return new AuctionVO(auction.userName(), auction.statusText(), price(auction.startingPrice()),
                    price(auction.buyoutPrice()), price(auction.topBid()), item == null ? null : item.name(),
                    String.join("，", specs), stats);
        }

        /**
         * 按订单方向计算当前页面使用的价格摘要。
         *
         * @param price 价格。
         * @return 按订单方向计算当前页面使用的价格摘要对应的文本。
         */
        private static String price(Integer price) {
            return price == null ? null : price + " 白金";
        }
    }
}
