package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RivenDefinitionsPluginTest {
    private final WarframeRivenDefinitionService service = mock(WarframeRivenDefinitionService.class);

    @Test
    void rejectsMalformedRepeatedUnknownAndNonTailOptionsBeforeQuery() {
        RivenDefinitionsPlugin plugin = plugin();
        for (String text : List.of("--mode", "--mode x", "--mode ATTRIBUTES", "--page 0", "--page -1",
                "--page 1.5", "--mode attributes text", "--mode attributes --mode challenges",
                "--page 1 --page 2", "--unknown 1", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(text)).successful()).as(text).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void preservesNumericKeywordAndPagesDefinitionsWithoutOverflow() {
        when(service.find("123", "attributes")).thenReturn(IntStream.range(0, 7).mapToObj(index ->
                new WarframeRivenDefinitionVO.EntryVO("/M", "Riven", "/M#riven-entry:" + index, "source",
                        new WarframeRivenDefinitionVO.AttributeVO("Damage", null, null, null, null,
                                false, null, List.of(new WarframeRivenDefinitionVO.VO(
                                new BigDecimal("0.0099999998"), null, null, true))), null)).toList());
        CommandResult result = plugin().handle(invocation("123 --mode=attributes --page 2"));
        assertThat(result.successful()).isTrue();
        assertThat(((WarframeRivenDefinitionVO) CommandReplyTestData.data(result)).items()).hasSize(1);
        assertThat(CommandReplyTestData.text(result)).contains("0.0099999998", "基础系数", "不可用", "未提供");
        verify(service).find("123", "attributes");
        assertThat(((WarframeRivenDefinitionVO) CommandReplyTestData.data(plugin().handle(
                invocation("123 --page=999999999999999999999")))).items()).isEmpty();
    }

    @Test
    void rendersChallengeAndZeroCountWithEveryAdoptedCondition() {
        when(service.find("", "challenges")).thenReturn(List.of(new WarframeRivenDefinitionVO.EntryVO(
                "/M", "Riven", "/M#riven-challenge:0", "source", null,
                new WarframeRivenDefinitionVO.ChallengeVO("/C", null, "Kill", null, null,
                        0, 0, BigDecimal.ZERO, List.of(new WarframeRivenDefinitionVO.ComplicationVO(
                        "/D", null, "Without damage", BigDecimal.ZERO, BigDecimal.ZERO))))));
        CommandResult result = plugin().handle(invocation("--page 1 --mode challenges"));
        assertThat(result.successful()).isTrue();
        assertThat(CommandReplyTestData.text(result)).contains("数量：0 ~ 0", "Without damage");
    }

    private RivenDefinitionsPlugin plugin() {
        RivenDefinitionsPlugin plugin = new RivenDefinitionsPlugin();
        ReflectionTestUtils.setField(plugin, "definitionService", service);
        return plugin;
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "riven-definitions", "紫卡", arguments);
    }
}
