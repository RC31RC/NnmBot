package com.nnm.bot.warframe.plugin.runtime.riven;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RivenWeeklyPluginTest {
    @Test
    void parsesTrailingOptionsInEitherOrderAndKeepsBareNumbersAsQuery() {
        WarframeRivenWeeklyService service = service();
        RivenWeeklyPlugin plugin = plugin(service);
        assertThat(plugin.handle(invocation("123 --platform PS4 --page=2 --rerolled false")).successful()).isTrue();
        verify(service).find("ps4", "123", false, 2);
        plugin.handle(invocation("AX-52 --rerolled=true --platform=SWI --page 999999999999999"));
        verify(service).find("swi", "AX-52", true, Integer.MAX_VALUE);
        plugin.handle(invocation(null));
        verify(service).find("pc", "", null, 1);
    }

    @Test
    void rejectsInvalidDuplicateAndMisplacedOptionsBeforeReading() {
        WarframeRivenWeeklyService service = mock(WarframeRivenWeeklyService.class);
        RivenWeeklyPlugin plugin = plugin(service);
        for (String arguments : List.of("--page 0", "--page -1", "--page 1.5", "--page",
                "--platform unknown", "--platform pc --platform ps4", "--rerolled 1", "--unknown true",
                "--page 2 AX-52", "AX-52 --rerolled true --rerolled false", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(arguments)).successful()).as(arguments).isFalse();
        }
        verifyNoInteractions(service);
    }

    private RivenWeeklyPlugin plugin(WarframeRivenWeeklyService service) {
        RivenWeeklyPlugin plugin = new RivenWeeklyPlugin();
        ReflectionTestUtils.setField(plugin, "weeklyService", service);
        return plugin;
    }

    private WarframeRivenWeeklyService service() {
        WarframeRivenWeeklyService service = mock(WarframeRivenWeeklyService.class);
        when(service.find(anyString(), anyString(), any(), anyInt())).thenReturn(
                new WarframeRivenWeeklyVO("", "pc", "1/1", "source", null, false, List.of(), null));
        return service;
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "riven-weekly", "紫卡周统计", arguments);
    }
}
