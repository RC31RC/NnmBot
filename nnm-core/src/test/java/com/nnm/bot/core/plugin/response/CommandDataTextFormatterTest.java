package com.nnm.bot.core.plugin.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CommandDataTextFormatterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private CommandDataTextFormatter formatter() {
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", mapper);
        return formatter;
    }

    @Test
    void readsNestedRecordDescriptionsWithoutRenamingJsonFields() {
        Orders data = new Orders(List.of(new Order("玩家甲", 29, "内部数据")));
        String text = formatter().format(data, "");
        assertThat(text).contains("订单:", "玩家: 玩家甲", "白金单价: 29")
                .doesNotContain("userName", "price", "内部数据");
        assertThat(mapper.valueToTree(data).path("orders").get(0).path("price").asInt())
                .isEqualTo(29);
    }

    @Test
    void preservesNullsArraysAndNonStringMapKeys() {
        String text = formatter().format(Map.of(1, Arrays.asList(null,
                new Order("玩家乙", 1, ""), new boolean[]{true, false})), "");
        assertThat(text).contains("未提供", "玩家: 玩家乙", "是", "否");
    }

    private record Orders(
            @JsonPropertyDescription("订单")
            List<Order> orders) {
    }

    @Test
    void neverHidesDataFieldsBasedOnTheirNamesOrSummary() {
        String text = formatter().format(Map.of(
                "title", "突击", "status", "可用", "source", "社区"), "突击：1 条");
        assertThat(text).contains("title: 突击", "status: 可用", "source: 社区");
    }

    @Test
    void inlineLayoutKeepsAllScalarValuesAndFallsBackForNestedObjects() {
        String text = formatter().format(List.of(new InlineRow("玩家", true, null),
                new NestedRow(new Order("玩家乙", 29, "内部数据"))), "");
        assertThat(text).contains("- 玩家: 玩家 | 启用: 是 | 说明: 未提供",
                "明细:", "玩家: 玩家乙", "白金单价: 29").doesNotContain("内部数据");
    }

    @CommandTextInline
    private record InlineRow(
            @JsonPropertyDescription("玩家")
            String player,
            @JsonPropertyDescription("启用")
            boolean enabled,
            @JsonPropertyDescription("说明")
            String description) {
    }

    @CommandTextInline
    private record NestedRow(
            @JsonPropertyDescription("明细")
            Order order) {
    }

    private record Order(
            @JsonPropertyDescription("玩家")
            String userName,
            @JsonProperty("price")
            @JsonPropertyDescription("白金单价")
            int platinum,
            @JsonIgnore
            String secret) {
    }
}
