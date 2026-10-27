package com.nnm.bot.warframe.plugin.persistent.official.world;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO.EntryVO;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeSynthTargetService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SynthTargetsPluginTest {
    private final WarframeSynthTargetService service = mock(WarframeSynthTargetService.class);
    private final SynthTargetsPlugin plugin = new SynthTargetsPlugin();

    SynthTargetsPluginTest() {
        ReflectionTestUtils.setField(plugin, "targetService", service);
    }

    @Test
    void rejectsMalformedDuplicateAndNonTailOptionsBeforeQuerying() {
        for (String query : List.of("--page 0", "--page -1", "--page x", "--page",
                "--page 1 --page 2", "--page=2 trailing", "--other 2", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(query)).successful()).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void numericSearchStaysAKeywordAndHugePagesCannotOverflow() {
        when(service.page("123", 2)).thenReturn(WarframeSynthTargetVO.page("123", List.of(), 2));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("123 --page=2")))).contains("没有找到");
        verify(service).page("123", 2);
        when(service.page("", Integer.MAX_VALUE)).thenReturn(
                WarframeSynthTargetVO.page("", List.of(), Integer.MAX_VALUE));
        assertThat(plugin.handle(invocation("--page 999999999999999999999")).successful()).isTrue();
    }

    @Test
    void preservesUnknownAndZeroProbabilityAndHistoricalVerificationDates() {
        EntryVO item = new WarframeSynthTargetVO.EntryVO("ancient_disruptor", "远古干扰者", "zh",
                List.of(new WarframeSynthTargetVO.LocationVO("Tikal", "地球", "挖掘", "感染者",
                        "6-16", "?%", LocalDate.of(2016, 6, 1)),
                        new WarframeSynthTargetVO.LocationVO("第二地点", null, null, null, null, "0%", null)),
                "source", "SOURCE_SELECTED");
        when(service.page("远古", 1)).thenReturn(WarframeSynthTargetVO.page("远古", List.of(item), 1));
        CommandResult result = plugin.handle(invocation("远古"));
        assertThat(CommandReplyTestData.data(result)).isInstanceOf(WarframeSynthTargetVO.class);
        assertThat(CommandReplyTestData.text(result)).contains("不代表今日目标", "?%", "0%", "2016-06-01", "验证日期：未提供")
                .doesNotContain("null", "SOURCE_SELECTED", "ancient_disruptor");
    }

    private CommandInvocation invocation(String query) {
        return new CommandInvocation(mock(BotMessageContext.class), "synth-targets", "合成目标", query);
    }
}
