package com.nnm.bot.warframe.plugin.persistent.official.worldstate;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.NightwaveSection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeNightwaveDefinitionVO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeNightwaveDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class NightwaveDefinitionsPluginTest {
    @Test
    void rejectsUnknownRepeatedMalformedAndOverlongOptionsWithoutQuerying() {
        WarframeNightwaveDefinitionService service = mock(WarframeNightwaveDefinitionService.class);
        NightwaveDefinitionsPlugin plugin = new NightwaveDefinitionsPlugin();
        ReflectionTestUtils.setField(plugin, "definitionService", service);
        for (String arguments : List.of("--section all", "--section", "--page", "--page 0", "--page -1", "--page x",
                "--page 1 --page 2", "--section rewards --section challenges", "--unknown all", "x".repeat(501))) {
            assertThat(plugin.handle(invocation(arguments)).successful()).as(arguments).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void supportsSectionAndPageInEitherOrderAndClampsOverflowWithoutMixingDynamicCommand() {
        WarframeNightwaveDefinitionService service = mock(WarframeNightwaveDefinitionService.class);
        NightwaveDefinitionsPlugin plugin = new NightwaveDefinitionsPlugin();
        ReflectionTestUtils.setField(plugin, "definitionService", service);
        String query = "/Lotus/" + "A".repeat(300);
        when(service.query(query, NightwaveSection.REWARDS, 2)).thenReturn(
                WarframeNightwaveDefinitionVO.page(query, NightwaveSection.REWARDS, List.of("Season"), List.of(), 2));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("--page=2 " + query + " --section rewards")))).contains("电波静态奖励");
        verify(service).query(query, NightwaveSection.REWARDS, 2);
        when(service.query("", NightwaveSection.CHALLENGES, Integer.MAX_VALUE)).thenReturn(
                WarframeNightwaveDefinitionVO.page("", NightwaveSection.CHALLENGES, List.of(), List.of(), Integer.MAX_VALUE));
        assertThat(((WarframeNightwaveDefinitionVO) CommandReplyTestData.data(plugin.handle(invocation("--page 99999999999999999999")))).page())
                .isEqualTo(1);
        when(service.query("", NightwaveSection.CHALLENGES, 1)).thenReturn(
                WarframeNightwaveDefinitionVO.page("", NightwaveSection.CHALLENGES, List.of(), List.of(), 1));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation(null)))).contains("没有匹配").doesNotContain("null");
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "nightwave-definitions", "电波静态定义", arguments);
    }
}
