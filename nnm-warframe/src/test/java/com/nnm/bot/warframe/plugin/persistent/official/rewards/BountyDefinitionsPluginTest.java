package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO.StageOptionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO.EntryVO;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BountyDefinitionsPluginTest {
    @Test
    void rejectsMalformedAndDuplicateOptionsWithoutDiscardingNumericSearch() {
        BountyDefinitionsPlugin plugin = new BountyDefinitionsPlugin();
        WarframeBountyDefinitionService service = mock(WarframeBountyDefinitionService.class);
        ReflectionTestUtils.setField(plugin, "bountyDefinitionService", service);
        for (String query : List.of("--page 0", "--page -1", "--page x", "--page", "--page 1 --page 2",
                "--page 1 text", "--kind normal", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(query)).successful()).isFalse();
        }
        verifyNoInteractions(service);
        when(service.page("123", 2)).thenReturn(WarframeBountyVO.page("123", List.of(), 2));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("123 --page=2")))).contains("没有找到");
        verify(service).page("123", 2);
    }

    @Test
    void hugePagesAreClampedAndStaticStagesRemainClearlyNamed() {
        BountyDefinitionsPlugin plugin = new BountyDefinitionsPlugin();
        WarframeBountyDefinitionService service = mock(WarframeBountyDefinitionService.class);
        ReflectionTestUtils.setField(plugin, "bountyDefinitionService", service);
        List<StageOptionDTO> stages = List.of(new WarframeBountyDefinitionDetailsDTO.StageOptionDTO(1, 2, "护送任务"));
        EntryVO entry = new WarframeBountyVO.EntryVO("/Jobs/Test", "赏金甲", null, null, null,
                "source", "SOURCE_SELECTED", stages);
        when(service.page("", Integer.MAX_VALUE))
                .thenReturn(WarframeBountyVO.page("", List.of(entry), Integer.MAX_VALUE));
        CommandResult result = plugin.handle(invocation("--page 99999999999999999"));
        assertThat(CommandReplyTestData.text(result)).contains("赏金静态定义", "赏金甲", "阶段 1 · 选项 2：护送任务")
                .doesNotContain("null", "SOURCE_SELECTED");
        assertThat(((WarframeBountyVO) CommandReplyTestData.data(result)).page()).isEqualTo(1);
    }

    private CommandInvocation invocation(String query) {
        return new CommandInvocation(mock(BotMessageContext.class), "bounty-definitions", "赏金定义", query);
    }
}
