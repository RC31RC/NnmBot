package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeItemResolution;
import com.nnm.bot.warframe.application.WarframeItemResolver;
import com.nnm.bot.warframe.domain.entity.WarframeItem;
import jakarta.annotation.Resource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/** 返回物品对应的灰机 Wiki 页面，不抓取 Wiki 内容。 */
@BotPluginCommand(
        code = WarframeCommandCodes.WIKI,
        matchType = MatchType.PREFIX,
        name = "Wiki",
        gameCode = "warframe",
        order = 20)
public class WikiPlugin extends BotPlugin {

    private static final String WIKI_BASE_URL = "https://warframe.huijiwiki.com/wiki/";

    @Resource
    private WarframeItemResolver itemResolver;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (StrUtil.isBlank(invocation.arguments())) {
            return CommandResult.text("Wiki 命令格式：wk 物品名称，例如：wk 电池");
        }
        WarframeItemResolution resolution = this.itemResolver.resolve(invocation.arguments());
        if (resolution.ambiguous()) {
            return CommandResult.text("名称存在多个候选，请输入更完整的名称："
                    + resolution.candidates().stream()
                            .map(WarframeItem::getLocalizedName)
                            .collect(Collectors.joining("、")));
        }
        if (!resolution.found() || StrUtil.isBlank(resolution.item().getWikiSlug())) {
            return CommandResult.text("没有找到对应的 Warframe Wiki 物品："
                    + invocation.arguments());
        }
        WarframeItem item = resolution.item();
        String encodedSlug = URLEncoder.encode(
                        item.getWikiSlug(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return CommandResult.text(item.getLocalizedName() + "\n" + WIKI_BASE_URL + encodedSlug);
    }

}
