package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.warframe.plugin.runtime.market.GoldTrashPlugin;
import com.nnm.bot.warframe.plugin.runtime.market.SilverTrashPlugin;
import com.nnm.bot.warframe.plugin.persistent.official.rewards.RelicsPlugin;

import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO.RelicVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.response.CommandDataTextFormatter;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRelicService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RelicPluginTest {
    @Test
    void longRowsHaveTheirOwnPageWithoutDroppingOrTruncatingText() {
        WarframeRelicDetailsDTO base = detail("Lith A1", 2, 2);
        WarframeRelicDetailsDTO record = new WarframeRelicDetailsDTO(base.relic(), base.rewards().stream()
                .map(row -> new WarframeRelicDetailsDTO.RewardDTO("Long reward ".repeat(70), row.rarity(), row.chance())).toList(),
                base.drops().stream().map(row -> new WarframeRelicDetailsDTO.DropDTO("Long location ".repeat(70), row.rarity(), row.chance())).toList());
        for (int page = 1; page <= 4; page++) {
            WarframeRelicVO display = WarframeRelicVO.relics("", List.of(record), page);
            RelicVO relic = display.items().getFirst();
            assertThat(relic.rewards().size() + relic.drops().size()).isEqualTo(1);
            assertThat(display.pagination()).isEqualTo(page + "/4 页，共 1 个遗物");
        }
        WarframeRelicVO first = WarframeRelicVO.rewards("", List.of(record), "Rare", 1);
        WarframeRelicVO second = WarframeRelicVO.rewards("", List.of(record), "Rare", 2);
        assertThat(first.rewards()).singleElement().satisfies(reward ->
                assertThat(reward.rewardName()).isEqualTo(record.rewards().getFirst().rewardName()));
        assertThat(second.rewards()).singleElement().satisfies(reward ->
                assertThat(reward.rewardName()).isEqualTo(record.rewards().getLast().rewardName()));
        assertThat(first.pagination()).isEqualTo("1/2 页，共 2 条奖励");
    }

    @Test
    void displaysLocationsAndUnknownMetadataWithoutDatabaseFields() throws Exception {
        WarframeRelicService service = mock(WarframeRelicService.class);
        WarframeRelicDetailsDTO detail = detail("Lith A1", 1, 1);
        when(service.findDetails("Lith A1")).thenReturn(List.of(detail));
        WarframeRelicVO display = (WarframeRelicVO) CommandReplyTestData.data(plugin(new RelicsPlugin(), service)
                .handle(invocation("Lith A1")));
        ObjectMapper mapper = new ObjectMapper();
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", mapper);

        assertThat(formatter.format(display, "遗物")).contains("Lith A1", "Hepit 0", "稀有", "0")
                .doesNotContain("/Lotus/", "recordKey", "parentId", "sourceCode", "currentJson");
        assertThat(mapper.writeValueAsString(display)).doesNotContain("/Lotus/", "recordKey",
                "parentBkey", "sourceCode", "currentJson", "\"chance\":null", "\"vaulted\":null");
        assertThat(display.items().getFirst().rewards().getFirst().chance()).isEqualByComparingTo("0");
        assertThat(display.items().getFirst().vaulted()).isNull();
    }

    @Test
    void pagesEveryRewardAndLocationIncludingAllDetailPagesOfTheSameRelic() {
        WarframeRelicDetailsDTO detail = detail("Lith A1", 6, 27);
        List<WarframeRelicDetailsDTO> records = List.of(detail, detail("Meso B1", 0, 0));
        List<String> rewards = new ArrayList<>();
        List<String> drops = new ArrayList<>();
        for (int page = 1; page <= 5; page++) {
            WarframeRelicVO display = WarframeRelicVO.relics("", records, page);
            assertThat(display.pagination()).isEqualTo(page + "/5 页，共 2 个遗物");
            assertThat(display.items()).hasSize(1);
            rewards.addAll(display.items().getFirst().rewards().stream()
                    .map(WarframeRelicVO.RewardVO::rewardName).toList());
            drops.addAll(display.items().getFirst().drops().stream()
                    .map(WarframeRelicVO.DropVO::location).toList());
        }
        assertThat(rewards).containsExactlyElementsOf(detail.rewards().stream()
                .map(WarframeRelicDetailsDTO.RewardDTO::rewardName).toList());
        assertThat(drops).containsExactlyElementsOf(detail.drops().stream()
                .map(WarframeRelicDetailsDTO.DropDTO::locationText).toList());
        assertThat(WarframeRelicVO.relics("", records, Integer.MAX_VALUE).notice())
                .contains("此页没有记录");
        assertThat(WarframeRelicVO.relics("", List.of(), 1).notice()).contains("没有找到");
    }

    @Test
    void keepsSameRewardInDifferentRelicsAndFiltersRarityCaseInsensitively() {
        WarframeRelicDetailsDTO first = detail("Lith A1", 1, 0);
        WarframeRelicDetailsDTO second = detail("Meso B1", 1, 0);
        WarframeRelicDetailsDTO uncommon = detail("Neo C1", 1, 0);
        uncommon = new WarframeRelicDetailsDTO(uncommon.relic(), List.of(new WarframeRelicDetailsDTO.RewardDTO("Forma 0", "UNCOMMON", BigDecimal.ZERO)), List.of());
        WarframeRelicVO gold = WarframeRelicVO.rewards("", List.of(first, second, uncommon), "Rare", 1);
        WarframeRelicVO silver = WarframeRelicVO.rewards("", List.of(first, second, uncommon), "Uncommon", 1);

        assertThat(gold.rewards()).hasSize(2).extracting(WarframeRelicVO.RewardVO::relicName)
                .containsExactly("Lith A1", "Meso B1");
        assertThat(gold.rewards()).extracting(WarframeRelicVO.RewardVO::rarity).containsOnly("稀有");
        assertThat(silver.rewards()).singleElement().satisfies(reward -> {
            assertThat(reward.relicName()).isEqualTo("Neo C1");
            assertThat(reward.rarity()).isEqualTo("罕见");
        });
        assertThat(WarframeRelicVO.rewards("Lith A1", List.of(first, second), "Rare", 1)
                .rewards()).hasSize(1);
    }

    @Test
    void rewardPagesKeepAllCountsAndDoNotOverflowOnHugePageInput() {
        List<WarframeRelicDetailsDTO> records = IntStream.range(0, 17).mapToObj(index -> detail("Lith A" + index, 1, 0)).toList();
        WarframeRelicVO page = WarframeRelicVO.rewards("", records, "Rare", 3);
        assertThat(page.pagination()).isEqualTo("3/3 页，共 17 条奖励");
        assertThat(page.rewards()).singleElement().extracting(WarframeRelicVO.RewardVO::relicName)
                .isEqualTo("Lith A16");
        WarframeRelicVO huge = WarframeRelicVO.rewards("", records, "Rare", Integer.MAX_VALUE);
        assertThat(huge.rewards()).isEmpty();
        assertThat(huge.notice()).contains("此页没有记录");
    }

    @Test
    void explicitPageDoesNotStealNumericNamesAndBadPagesNeverQueryTheService() {
        WarframeRelicService service = mock(WarframeRelicService.class);
        when(service.findDetails(anyString())).thenReturn(List.of());
        RelicsPlugin plugin = plugin(new RelicsPlugin(), service);
        plugin.handle(invocation("Lith A1 --page 2"));
        verify(service).findDetails("Lith A1");
        plugin.handle(invocation("123"));
        verify(service).findDetails("123");
        plugin.handle(invocation("Lith A1 2"));
        verify(service).findDetails("Lith A1 2");

        WarframeRelicService invalidService = mock(WarframeRelicService.class);
        RelicsPlugin invalid = plugin(new RelicsPlugin(), invalidService);
        for (String input : List.of("--page 0", "--page -1", "--page x", "--page",
                "Lith --page 2 text", "--page 1 --page 2")) {
            assertThat(invalid.handle(invocation(input)).successful()).isFalse();
        }
        verifyNoInteractions(invalidService);
        assertThat(GoldTrashPlugin.class.getAnnotation(BotPluginCommand.class).matchType())
                .isEqualTo(MatchType.PREFIX);
        assertThat(SilverTrashPlugin.class.getAnnotation(BotPluginCommand.class).matchType())
                .isEqualTo(MatchType.PREFIX);
    }

    private <T extends AbstractRelicPlugin> T plugin(T plugin, WarframeRelicService service) {
        ReflectionTestUtils.setField(plugin, "relicService", service);
        return plugin;
    }

    private CommandInvocation invocation(String arguments) {
        return new CommandInvocation(mock(BotMessageContext.class), "relics", "遗物", arguments);
    }

    private WarframeRelicDetailsDTO detail(String name, int rewards, int drops) {
        var parent = new WarframeRelicDetailsDTO.RelicDTO("/Lotus/" + name, name, null, null, null, null, "NNM_DATASET");
        var rewardList = IntStream.range(0, rewards).mapToObj(index ->
                new WarframeRelicDetailsDTO.RewardDTO("Forma " + index, "rARE", BigDecimal.ZERO)).toList();
        var dropList = IntStream.range(0, drops).mapToObj(index ->
                new WarframeRelicDetailsDTO.DropDTO("Hepit " + index, null, null)).toList();
        return new WarframeRelicDetailsDTO(parent, rewardList, dropList);
    }
}
