package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO.EntryVO;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeCompanionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompanionsPluginTest {
    private final WarframeCompanionService service = mock(WarframeCompanionService.class);
    private final CompanionsPlugin plugin = new CompanionsPlugin();

    CompanionsPluginTest() {
        ReflectionTestUtils.setField(plugin, "companionService", service);
    }

    @Test
    void rejectsInvalidFlagsBeforeQuerying() {
        for (String input : List.of("--page", "--page 0", "--page -1", "--page=x", "--page 1 --page 2",
                "--page 2 extra", "--page 2147483648", "--type pets", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(input)).successful()).as(input).isFalse();
        }
        verifyNoInteractions(service);
    }

    @Test
    void clampsLargePageAndResolvesImagesOnlyForVisibleEntries() {
        List<EntryVO> entries = IntStream.range(0, 8).mapToObj(index -> new WarframeCompanionVO.EntryVO(
                "/Lotus/Pets/" + index, "Pet " + index, "Description", "Pets", "KubrowPets", 0, false,
                BigDecimal.ZERO, null, null, null, null, null, List.of(), List.of(),
                null, "{}", "image", null, "source", "SOURCE_SELECTED")).toList();
        when(service.find("pets")).thenReturn(entries);
        when(service.withImage(any())).thenAnswer(call -> call.getArgument(0));
        CommandResult result = plugin.handle(invocation("pets --page=2147483647"));
        WarframeCompanionVO display = (WarframeCompanionVO) CommandReplyTestData.data(result);
        assertThat(display.page()).isEqualTo(2);
        assertThat(display.items()).hasSize(2);
        assertThat(CommandReplyTestData.text(result)).contains("Pet 6", "Pet 7", "生命：0").doesNotContain("Pet 0", "护盾");
        verify(service, times(2)).withImage(any());
    }

    @Test
    void emptyQueryAndResultsProduceStableNotice() {
        when(service.find("")).thenReturn(List.of());
        CommandResult result = plugin.handle(invocation(null));
        assertThat(result.successful()).isTrue();
        assertThat(CommandReplyTestData.text(result)).contains("没有找到匹配的同伴资料");
        verify(service, never()).withImage(any());
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "companions", "同伴", arguments);
    }
}
