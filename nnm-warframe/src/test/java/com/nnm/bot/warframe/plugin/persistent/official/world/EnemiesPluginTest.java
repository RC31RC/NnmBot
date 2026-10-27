package com.nnm.bot.warframe.plugin.persistent.official.world;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeEnemyVO;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeEnemyService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EnemiesPluginTest {
    @Test
    void validatesOptionsBeforeQueryAndSupportsAllSectionsAndOverflow() {
        WarframeEnemyService service = mock(WarframeEnemyService.class);
        EnemiesPlugin plugin = new EnemiesPlugin();
        ReflectionTestUtils.setField(plugin, "enemyService", service);
        for (String invalid : List.of("--section all", "--section", "--page", "--page 0", "--page -1", "--page x",
                "--page 1 --page 2", "--section agents --section avatars", "--unknown x", "x".repeat(501))) {
            assertThat(plugin.handle(invocation(invalid)).successful()).as(invalid).isFalse();
        }
        verifyNoInteractions(service);
        for (EnemySection section : EnemySection.values()) {
            when(service.query("/Enemy/A", section, 2)).thenReturn(
                    WarframeEnemyVO.page("/Enemy/A", section, List.of(), 2));
            assertThat(CommandReplyTestData.text(plugin.handle(invocation("--page=2 /Enemy/A --section="
                    + section.name().toLowerCase().replace('_', '-'))))).contains("没有匹配").doesNotContain("null");
            verify(service).query("/Enemy/A", section, 2);
        }
        when(service.query("", EnemySection.AVATARS, Integer.MAX_VALUE)).thenReturn(
                WarframeEnemyVO.page("", EnemySection.AVATARS, List.of(), Integer.MAX_VALUE));
        assertThat(((WarframeEnemyVO) CommandReplyTestData.data(plugin.handle(invocation("--page 99999999999999999999")))).page()).isEqualTo(1);
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "enemies", "敌人资料", arguments);
    }
}
