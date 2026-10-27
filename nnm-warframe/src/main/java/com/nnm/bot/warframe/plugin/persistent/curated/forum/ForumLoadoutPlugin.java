package com.nnm.bot.warframe.plugin.persistent.curated.forum;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumProperties;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticleLinkVO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleSearchQuery;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumArticleService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 查询管理端维护的配装文章，只返回文章地址，不把正文塞进机器人消息。
 */
@BotPluginCommand(
        code = WarframeCommandCodes.FORUM_LOADOUT,
        matchType = MatchType.PREFIX,
        name = "wf配装",
        gameCode = "warframe",
        order = 21)
// wf_forum_article：按标题、分类和标签检索文章 URL；正文只保留在文章页面，不作为机器人数据返回。
public class ForumLoadoutPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.forum.articles"));
    }

    @Resource
    private ForumArticleService articleService;

    @Resource
    private ForumProperties forumProperties;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (StrUtil.isBlank(invocation.arguments())) {
            return CommandResult.text("wf配装命令格式：wf配装 关键词");
        }
        ForumArticleSearchQuery query = parseQuery(invocation.arguments());
        List<ForumArticleVO> articles = articleService.searchEnabled(query);
        if (CollUtil.isEmpty(articles)) {
            return CommandResult.text("没有找到匹配的 Warframe 配装文章："
                    + invocation.arguments());
        }
        return CommandResult.view("warframe.forum.articles", "找到 " + articles.size() + " 篇相关配装文章",
                articles.stream().map(article -> new ForumArticleLinkVO(
                        article.title(),
                        StrUtil.blankToDefault(article.category(), "未分类"),
                        forumProperties.publicArticleUrl(article.id()))).toList());
    }

    /**
     * 支持“标题:”、“分类:”和“标签:”字段条件；没有前缀的多个词要求同时命中，
     *   这样既兼容简单的 wf配装 词条，也能精确筛选分类和标签。
     *
     * @param raw 原始。
     * @return 论坛文章搜索查询对象。
     */
    private static ForumArticleSearchQuery parseQuery(String raw) {
        List<String> keywords = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        StringBuilder title = new StringBuilder();
        StringBuilder category = new StringBuilder();
        List<String> tokens = tokenize(raw);
        for (int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);
            int separator = fieldSeparator(token);
            if (separator < 1) {
                keywords.add(token);
                continue;
            }
            String prefix = token.substring(0, separator).toLowerCase(Locale.ROOT);
            String value = token.substring(separator + 1).trim();
            // 兼容“标题: "带空格的标题"”这种写法；tokenizer 会把引号内容保留为一个 token。
            if (StrUtil.isBlank(value) && index + 1 < tokens.size()) {
                value = tokens.get(++index);
            }
            switch (prefix) {
                case "标题", "title" -> append(title, value);
                case "分类", "category", "分类名" -> append(category, value);
                case "标签", "tag", "tags" -> splitTags(value, tags);
                default -> keywords.add(token);
            }
        }
        return new ForumArticleSearchQuery(
                title.toString(), category.toString(), tags, keywords);
    }

    /**
     * 一个标签字段可以携带多个完整标签，例如：
     *    {@code 标签:melee,steel-path} 或 {@code 标签:melee、steel-path}。
     *    标题和分类不在这里拆分，因为它们允许包含空格。
     *
     * @param value 待校验或规范化的文本。
     * @param target 本次操作的目标对象或路径。
     */
    private static void splitTags(String value, List<String> target) {
        for (String tag : StrUtil.emptyIfNull(value).split("[,，、\\s]+")) {
            if (StrUtil.isNotBlank(tag)) {
                target.add(tag.trim());
            }
        }
    }

    /**
     * 按聊天命令语法切分参数：连续空白分隔普通词，单引号和双引号保留其中的空格。
     *   未闭合引号也按普通文本收尾，避免用户输入错误导致整个插件抛异常。
     *
     * @param raw 原始。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> tokenize(String raw) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char quote = 0;
        boolean escaped = false;
        for (char current : StrUtil.emptyIfNull(raw).trim().toCharArray()) {
            if (escaped) {
                token.append(current);
                escaped = false;
                continue;
            }
            if (current == '\\' && quote != 0) {
                escaped = true;
                continue;
            }
            if (quote != 0) {
                if (current == quote) {
                    quote = 0;
                } else {
                    token.append(current);
                }
                continue;
            }
            if (current == '\'' || current == '"') {
                quote = current;
            } else if (Character.isWhitespace(current)) {
                if (StrUtil.isNotEmpty(token)) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(current);
            }
        }
        if (escaped) {
            token.append('\\');
        }
        if (StrUtil.isNotEmpty(token)) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    /**
     * 查找命令词中首个半角或全角冒号，确定字段名与值的分界。
     *
     * @param token 已完成引号和转义处理的单个命令词。
     * @return 首个字段分隔符的下标；不存在时返回 -1。
     */
    private static int fieldSeparator(String token) {
        int ascii = token.indexOf(':');
        int fullWidth = token.indexOf('：');
        if (ascii < 0) {
            return fullWidth;
        }
        if (fullWidth < 0) {
            return ascii;
        }
        return Math.min(ascii, fullWidth);
    }

    /**
     * 将一段字段值追加到目标文本，已有内容时先补一个空格。
     *
     * @param target 本次操作的目标对象或路径。
     * @param value 待校验或规范化的文本。
     */
    private static void append(StringBuilder target, String value) {
        if (StrUtil.isNotEmpty(target)) {
            target.append(' ');
        }
        target.append(value);
    }
}
