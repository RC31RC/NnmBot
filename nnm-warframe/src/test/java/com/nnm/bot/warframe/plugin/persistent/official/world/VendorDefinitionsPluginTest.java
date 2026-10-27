package com.nnm.bot.warframe.plugin.persistent.official.world;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeVendorDefinitionVO;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeVendorDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class VendorDefinitionsPluginTest {
    @Test
    void rejectsMalformedUnknownDuplicateAndOverlongOptionsBeforeQuery() {
        WarframeVendorDefinitionService service = mock(WarframeVendorDefinitionService.class);
        VendorDefinitionsPlugin plugin = new VendorDefinitionsPlugin();
        ReflectionTestUtils.setField(plugin, "definitionService", service);
        for (String arguments : List.of("--section all", "--section", "--page", "--page 0", "--page -1", "--page x",
                "--page 1 --page 2", "--section items --section random-prices", "--unknown x", "x".repeat(501))) {
            assertThat(plugin.handle(invocation(arguments)).successful()).as(arguments).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void handlesKeywordOptionOrderAndOverflowWithoutImplyingLiveInventory() {
        WarframeVendorDefinitionService service = mock(WarframeVendorDefinitionService.class);
        VendorDefinitionsPlugin plugin = new VendorDefinitionsPlugin();
        ReflectionTestUtils.setField(plugin, "definitionService", service);
        String query = "/Vendors/" + "A".repeat(300);
        when(service.query(query, VendorSection.RANDOM_PRICES, 2)).thenReturn(
                WarframeVendorDefinitionVO.page(query, VendorSection.RANDOM_PRICES, List.of(), List.of(), 2));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("--page=2 " + query + " --section=random-prices"))))
                .contains("商人静态随机价格", "不代表当前库存");
        verify(service).query(query, VendorSection.RANDOM_PRICES, 2);
        when(service.query("", VendorSection.ITEMS, Integer.MAX_VALUE)).thenReturn(
                WarframeVendorDefinitionVO.page("", VendorSection.ITEMS, List.of(), List.of(), Integer.MAX_VALUE));
        assertThat(((WarframeVendorDefinitionVO) CommandReplyTestData.data(plugin.handle(invocation("--page 999999999999999999999")))).page())
                .isEqualTo(1);
        when(service.query("", VendorSection.ITEMS, 1)).thenReturn(
                WarframeVendorDefinitionVO.page("", VendorSection.ITEMS, List.of(), List.of(), 1));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation(null)))).contains("没有匹配").doesNotContain("null");
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "vendor-definitions", "商人静态定义", arguments);
    }
}
