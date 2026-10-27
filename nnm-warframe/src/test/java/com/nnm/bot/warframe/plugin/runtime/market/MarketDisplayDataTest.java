package com.nnm.bot.warframe.plugin.runtime.market;

import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.core.plugin.response.CommandDataTextFormatter;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeMarketOrderVO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderEntryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class MarketDisplayDataTest {

    @Test
    void compactCommandDataKeepsEachOrderOnceAndFitsOneTextMessage() {
        ObjectMapper mapper = new ObjectMapper();
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", mapper);
        List<WarframeMarketOrderEntryDTO> orders = IntStream.range(0, 5).mapToObj(index ->
                new WarframeMarketOrderEntryDTO("internal-" + index, "sell", "Player" + index, "8",
                        68 + index, 17, 1, null, null, null, null, null, "ingame", "游戏中"))
                .toList();
        WarframeMarketOrderPageDTO result = new WarframeMarketOrderPageDTO("Ash Prime 一套", "ash", "ash_prime_set",
                "pc", "ALL", false, null, PageResult.of(orders, 1, 5, 56),
                "68 白金", "47 白金", null, List.of());
        WarframeMarketOrderVO display = WarframeMarketOrderVO.from(result);

        String text = formatter.format(display, "市场订单");

        assertThat(text.length()).isLessThan(1800);
        assertThat(text).contains("分页: 1/12 页，共 56 单", "条件: 买家及卖家",
                        "- 方向: 出售 | 玩家: Player0 | 单价: 68 白金 | 数量: 17 | 状态: 游戏中")
                .doesNotContain("未提供", "规格:", "物品详情", "套装部件", "internal-", "ash_prime_set");
        for (int index = 0; index < 5; index++) {
            assertThat(text).containsOnlyOnce("Player" + index);
        }
        JsonNode json = mapper.valueToTree(display);
        assertThat(json.properties()).extracting(Map.Entry::getKey)
                .containsExactly("itemName", "platform", "filter", "pagination",
                        "lowestSellPrice", "highestBuyPrice", "orders");
        assertThat(json.path("orders").get(0).properties()).extracting(Map.Entry::getKey)
                .containsExactly("direction", "userName", "price", "quantity", "status");
    }

    @Test
    void compactDataPreservesRelevantTradeSpecificationsAndEmptyPageNotice() {
        WarframeMarketOrderEntryDTO order = new WarframeMarketOrderEntryDTO("id", "buy", "Buyer", "8",
                29, 17, 6, "subtype", 0, 2, 1, 3, "online", "在线");
        WarframeMarketOrderPageDTO result = new WarframeMarketOrderPageDTO("物品", "query", "slug",
                "pc", "BUY", true, 10, PageResult.of(List.of(order), 1, 5, 1),
                "", "29 白金", null, List.of());
        WarframeMarketOrderVO display = WarframeMarketOrderVO.from(result);

        assertThat(display.filter()).isEqualTo("买家，满级");
        assertThat(display.orders().getFirst().direction()).isEqualTo("收购");
        assertThat(display.orders().getFirst().specifications()).contains(
                "等级 0", "subtype", "剩余 2 次", "琥珀星 1", "青蓝星 3", "每次交易 6 个");
        assertThat(display.notice()).isNull();

        WarframeMarketOrderPageDTO emptyPage = new WarframeMarketOrderPageDTO("物品", "query", "slug",
                "pc", "SELL", false, null, PageResult.of(List.of(), 2, 5, 1),
                "", "", null, List.of());
        assertThat(WarframeMarketOrderVO.from(emptyPage).notice())
                .isEqualTo("此页没有订单，请查询前面的页码");
        WarframeMarketOrderPageDTO noOrders = new WarframeMarketOrderPageDTO("物品", "query", "slug",
                "pc", "ALL", false, null, PageResult.of(List.of(), 1, 5, 0),
                "", "", null, List.of());
        assertThat(WarframeMarketOrderVO.from(noOrders).notice())
                .isEqualTo("没有符合条件的在线订单");
    }

    @Test
    void formatsRealOrdersIncludingComputedPropertiesWithoutChangingTemplateKeys() {
        ObjectMapper mapper = new ObjectMapper();
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", mapper);
        WarframeMarketOrderEntryDTO order = new WarframeMarketOrderEntryDTO("id", "sell", "Pawcan", "8",
                29, 17, 1, null, null, null, null, null, "ingame", "游戏中");
        WarframeMarketOrderPageDTO data = new WarframeMarketOrderPageDTO("Ash Prime", "ash", "ash_prime_set",
                "pc", "sell", false, null, PageResult.of(List.of(order), 1, 10, 1L),
                "29", "", null, List.of());

        String text = formatter.format(data, "市场订单");

        assertThat(text).contains("订单:", "玩家: Pawcan", "白金单价: 29",
                "在线状态: 游戏中", "当前页: 1", "每页数量: 10")
                .doesNotContain("userName:", "platinum:", "orderPage:");
        JsonNode json = mapper.valueToTree(data);
        assertThat(json.path("orders").get(0).path("platinum").asInt()).isEqualTo(29);
        assertThat(json.path("page").asInt()).isEqualTo(1);
        assertThat(json.has("orderPage")).isFalse();
    }
}
