package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenUnrollableVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenUnrollableService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RivenUnrollablesPluginTest {
    private final WarframeRivenUnrollableService service = mock(WarframeRivenUnrollableService.class);

    @Test
    void rejectsInvalidRepeatedNonTailAndUnknownOptionsBeforeQuery() {
        for (String text : List.of("--page", "--page 0", "--page -1", "--page 1.5", "--page x",
                "--page 2 text", "--page 1 --page 2", "--unknown 1", "--page 999999999999999999",
                "x".repeat(201))) {
            assertThat(plugin().handle(invocation(text)).successful()).as(text).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void pagesRelationsAndClampsLargeValidPagesWithoutOverflow() {
        when(service.find("123")).thenReturn(IntStream.range(0, 9).mapToObj(index ->
                new WarframeRivenUnrollableVO.EntryVO("/Lotus/W/" + index, "Weapon " + index,
                        "Impact", "source", "SOURCE_SELECTED")).toList());
        CommandResult result = plugin().handle(invocation("123 --page=2147483647"));
        assertThat(result.successful()).isTrue();
        assertThat(((WarframeRivenUnrollableVO) CommandReplyTestData.data(result)).items()).hasSize(1);
        assertThat(CommandReplyTestData.text(result)).contains("2/2", "Weapon 8", "Impact");
        verify(service).find("123");
    }

    @Test
    void emptyResultsDoNotClaimAttributeCanBeRolled() {
        when(service.find("")).thenReturn(List.of());
        assertThat(CommandReplyTestData.text(plugin().handle(invocation("")))).contains("不能据此判断属性可洗");
    }

    private RivenUnrollablesPlugin plugin() {
        RivenUnrollablesPlugin plugin = new RivenUnrollablesPlugin();
        ReflectionTestUtils.setField(plugin, "service", service);
        return plugin;
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "riven-unrollables", "紫卡", arguments);
    }
}
