package com.nnm.bot.warframe.plugin.persistent.curated.watch;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WarframeSubscriptionSpecTest {
    @Test
    void acceptsSingleTopicsAndCanonicalizesAlternativeFilters() {
        assertThat(WarframeSubscriptionSpec.parse("平原"))
                .isEqualTo(new WarframeSubscriptionSpec("cetus-night", "filter", "*"));
        assertThat(WarframeSubscriptionSpec.parse("仲裁 谷神防御 谷神拦截 谷神防御"))
                .isEqualTo(WarframeSubscriptionSpec.parse("仲裁 谷神拦截|谷神防御"));
        assertThat(WarframeSubscriptionSpec.parse("裂缝 钢铁虚空捕获 虚空歼灭古纪").value())
                .contains("钢铁虚空捕获", "虚空歼灭古纪");
        assertThat(WarframeSubscriptionSpec.parse("裂缝 H2星云 恩斯尤区格虹桥回声").value())
                .contains("h2星云", "恩斯尤区格虹桥回声");
    }

    @Test
    void rejectsUnknownTopicsAmbiguousWildcardsAndUnboundedInput() {
        assertThat(WarframeSubscriptionSpec.parse("" )).isNull();
        assertThat(WarframeSubscriptionSpec.parse("未知")).isNull();
        assertThat(WarframeSubscriptionSpec.parse("奸商 明天")).isNull();
        assertThat(WarframeSubscriptionSpec.parse("裂缝 * 虚空")).isNull();
        assertThat(WarframeSubscriptionSpec.parse("裂缝 " + "a".repeat(501))).isNull();
    }
}
