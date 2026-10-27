package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeLinkVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WikiPluginTest {

    @Mock
    private WarframeItemResolverService itemResolver;
    @InjectMocks
    private WikiPlugin plugin;

    @Test
    void returnsUrlEncodedWikiLinkWithoutRequestingWikiContent() {
        WarframeItemDTO item = item("奥罗金电池", "Orokin电池");
        when(this.itemResolver.resolve("电池"))
                .thenReturn(WarframeItemResolutionDTO.resolved(item));

        CommandResult result = this.plugin.handle(invocation("电池"));

        assertThat(CommandReplyTestData.text(result)).isEqualTo("Warframe Wiki");
        assertThat(CommandReplyTestData.data(result)).isEqualTo(new WarframeLinkVO(
                "奥罗金电池", "https://warframe.huijiwiki.com/wiki/Orokin%E7%94%B5%E6%B1%A0"));
    }

    @Test
    void returnsCandidateNamesWhenAliasIsAmbiguous() {
        when(this.itemResolver.resolve("甲"))
                .thenReturn(WarframeItemResolutionDTO.ambiguous(List.of(
                        item("甲 Prime", "甲_Prime"),
                        item("甲", "甲"))));

        CommandResult result = this.plugin.handle(invocation("甲"));

        assertThat(CommandReplyTestData.text(result)).contains("多个候选", "甲 Prime、甲");
        assertThat(result.successful()).isFalse();
        assertThat(result.actionContext()).isEmpty();
    }

    @Test
    void returnsReadableMessageWhenItemIsMissing() {
        when(this.itemResolver.resolve("未知物品"))
                .thenReturn(WarframeItemResolutionDTO.notFound());

        CommandResult result = this.plugin.handle(invocation("未知物品"));

        assertThat(CommandReplyTestData.text(result)).contains("没有找到", "未知物品");
        assertThat(result.successful()).isFalse();
    }

    private static CommandInvocation invocation(String arguments) {
        BotMessageContext context = BotMessageContext.of(
                BotPlatform.ONEBOT,
                BotMessageScope.GROUP,
                "10001",
                "20001",
                "30001",
                "40001",
                "wk " + arguments,
                "wk " + arguments,
                null);
        return new CommandInvocation(context, "wiki", "wk", arguments);
    }

    private static WarframeItemDTO item(String localizedName, String wikiSlug) {
        WarframeItemDTO item = new WarframeItemDTO();
        item.setOfficialName(localizedName);
        item.setLocalizedName(localizedName);
        item.setWikiSlug(wikiSlug);
        item.setEnabled(true);
        return item;
    }
}
