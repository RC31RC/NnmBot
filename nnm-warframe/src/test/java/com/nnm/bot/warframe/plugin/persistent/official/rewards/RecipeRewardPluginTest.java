package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.warframe.plugin.persistent.official.crafting.RecipesPlugin;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeRecipeService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RecipeRewardPluginTest {
    @Test
    void recipeOptionsPreserveNumericSearchAndZeroCostsWithoutExposingPaths() {
        WarframeRecipeService service = mock(WarframeRecipeService.class);
        RecipesPlugin plugin = new RecipesPlugin();
        ReflectionTestUtils.setField(plugin, "recipeService", service);
        WarframeRecipeVO.EntryVO item = new WarframeRecipeVO.EntryVO("/Lotus/Recipe", "/Lotus/Recipe", "测试蓝图",
                "dojo", "research", "/Lotus/Product", "测试产物", 0L, 0L, 0L, 0L, 0L,
                "source_clan_scale", List.of(new WarframeRecipeVO.IngredientVO(
                "/Lotus/Material", "测试材料", 0)), null, null, "source", "SOURCE_SELECTED");
        when(service.page(anyString(), any(), anyInt()))
                .thenReturn(WarframeRecipeVO.page("123", List.of(item), 1));
        CommandResult result = plugin.handle(invocation("123 --kind=dojo --page 1"));
        assertThat(result.successful()).isTrue();
        assertThat(CommandReplyTestData.text(result)).contains("测试材料 × 0", "制作时间：0 秒", "信用点：0", "原导出氏族规模")
                .doesNotContain("/Lotus/", "SOURCE_SELECTED", "null");
        verify(service).page("123", "dojo", 1);
    }

    @Test
    void rejectsBadRecipeOptionsBeforeCallingService() {
        WarframeRecipeService service = mock(WarframeRecipeService.class);
        RecipesPlugin plugin = new RecipesPlugin();
        ReflectionTestUtils.setField(plugin, "recipeService", service);
        for (String query : List.of("--page 0", "--page -1", "--page x", "--page",
                "--page 1 --page 2", "--kind dojo --kind ordinary", "--kind invalid",
                "--kind ordinary text", "--unknown 1", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(query)).successful()).isFalse();
        }
        verifyNoInteractions(service);
        when(service.page("", null, Integer.MAX_VALUE)).thenReturn(
                WarframeRecipeVO.page("", List.of(), Integer.MAX_VALUE));
        assertThat(plugin.handle(invocation("--page 99999999999999999999")).successful()).isTrue();
    }

    @Test
    void rewardStagesAndPagesAreBoundedAndRejectDuplicateOrNonTailOptions() {
        WarframeRewardPoolService service = mock(WarframeRewardPoolService.class);
        RewardPoolsPlugin plugin = new RewardPoolsPlugin();
        ReflectionTestUtils.setField(plugin, "rewardPoolService", service);
        for (String query : List.of("--stage 0", "--page 0", "--stage -1", "--stage x",
                "--stage 999999999999999999999", "--stage 1 text",
                "--stage 1 --stage 2", "--page 1 --page 2", "--unknown 1", "x".repeat(201))) {
            assertThat(plugin.handle(invocation(query)).successful()).isFalse();
        }
        verifyNoInteractions(service);
        List<WarframeRewardPoolVO.EntryVO> entries = IntStream.range(0, 13).mapToObj(index ->
                new WarframeRewardPoolVO.EntryVO("/Lotus/Pool", "测试奖励池", 2, "/Lotus/Item",
                        "测试奖励", 0, index == 12 ? BigDecimal.ZERO : null, null,
                        false, "source", "SOURCE_SELECTED")).toList();
        when(service.find("", 2)).thenReturn(entries);
        CommandResult result = plugin.handle(invocation("--stage=2 --page 2"));
        assertThat(((WarframeRewardPoolVO) CommandReplyTestData.data(result)).items()).hasSize(1);
        assertThat(CommandReplyTestData.text(result)).contains("阶段 2", "测试奖励 × 0", "0%")
                .doesNotContain("/Lotus/", "null");
        assertThat(((WarframeRewardPoolVO) CommandReplyTestData.data(plugin.handle(
                invocation("--stage 2 --page 99999999999999999999")))).items()).isEmpty();
    }

    private CommandInvocation invocation(String query) {
        return new CommandInvocation(mock(BotMessageContext.class), "recipes", "配方", query);
    }
}
