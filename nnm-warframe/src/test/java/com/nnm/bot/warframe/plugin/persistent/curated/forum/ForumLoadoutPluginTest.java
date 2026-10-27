package com.nnm.bot.warframe.plugin.persistent.curated.forum;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumProperties;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleLinkVO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleSearchQuery;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumLoadoutPluginTest {

    @Mock
    private ForumArticleService articleService;

    @Mock
    private ForumProperties forumProperties;

    @InjectMocks
    private ForumLoadoutPlugin plugin;

    @Test
    void asksForKeywordWhenArgumentsAreBlank() {
        CommandResult result = this.plugin.handle(invocation(""));

        assertThat(CommandReplyTestData.text(result)).isEqualTo("wf配装命令格式：wf配装 关键词");
    }

    @Test
    void reportsWhenNoArticleMatches() {
        when(this.articleService.searchEnabled(new ForumArticleSearchQuery(
                "",
                "",
                List.of(),
                List.of("赤毒")))).thenReturn(List.of());

        CommandResult result = this.plugin.handle(invocation("赤毒"));

        assertThat(CommandReplyTestData.text(result)).contains("没有找到", "赤毒");
    }

    @Test
    void returnsAllMatchingArticleTitlesAndUrlsInServiceOrder() {
        when(this.articleService.searchEnabled(new ForumArticleSearchQuery(
                "",
                "",
                List.of(),
                List.of("p")))).thenReturn(List.of(
                article("钢铁之路配装", 1L),
                article("火卫二配装", 2L)));
        when(this.forumProperties.publicArticleUrl(1L))
                .thenReturn("https://example.test/steel");
        when(this.forumProperties.publicArticleUrl(2L))
                .thenReturn("https://example.test/deimos");

        CommandResult result = this.plugin.handle(invocation("p"));

        assertThat(CommandReplyTestData.text(result)).isEqualTo("找到 2 篇相关配装文章");
        assertThat(CommandReplyTestData.data(result)).isEqualTo(List.of(
                new ForumArticleLinkVO(
                        "钢铁之路配装", "配装", "https://example.test/steel"),
                new ForumArticleLinkVO(
                        "火卫二配装", "配装", "https://example.test/deimos")));
    }

    @Test
    void splitsMultipleTagsFromOneFieldWithoutSplittingTitleOrCategory() {
        when(this.articleService.searchEnabled(new ForumArticleSearchQuery(
                "钢铁 之路",
                "近战 配装",
                List.of("melee", "steel-path", "heavy"),
                List.of("赤毒")))).thenReturn(List.of(
                article("钢铁之路配装", 1L)));
        when(this.forumProperties.publicArticleUrl(1L))
                .thenReturn("https://example.test/steel");

        CommandResult result = this.plugin.handle(invocation(
                "标题:\"钢铁 之路\" 分类:\"近战 配装\" 标签:melee,steel-path、heavy 赤毒"));

        assertThat(CommandReplyTestData.text(result)).isEqualTo("找到 1 篇相关配装文章");
        assertThat(CommandReplyTestData.data(result)).isEqualTo(List.of(
                new ForumArticleLinkVO(
                        "钢铁之路配装", "配装", "https://example.test/steel")));
    }

    private static CommandInvocation invocation(String arguments) {
        BotMessageContext context = BotMessageContext.of(
                BotPlatform.ONEBOT,
                BotMessageScope.GROUP,
                "10001",
                "20001",
                "30001",
                "40001",
                "wf配装 " + arguments,
                "wf配装 " + arguments,
                null);
        return new CommandInvocation(context, "forum-loadout", "wf配装", arguments);
    }

    private static ForumArticleVO article(String title, long id) {
        return new ForumArticleVO(
                id,
                "article",
                title,
                null,
                null,
                "配装",
                null,
                null,
                null,
                "/#/warframe/forum/detail/" + id,
                true,
                1,
                100L,
                false,
                false,
                null,
                null);
    }
}
