package com.nnm.bot.warframe.plugin.persistent.official.reference;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 返回物品对应的灰机 Wiki 页面，不抓取 Wiki 内容。 */
@BotPluginCommand(code = WarframeCommandCodes.WIKI, matchType = MatchType.PREFIX)
// 不抓取 Wiki 内容；仅使用官方名称与人工别名/术语白名单解析页面名。
public class WikiPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }

    private static final String WIKI_BASE_URL = "https://warframe.huijiwiki.com/wiki/";

    @Resource
    private WarframeItemResolverService itemResolver;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        if (StrUtil.isBlank(parsed)) {
            return CommandResult.rejected("Wiki 命令格式：wk 物品名称，例如：wk 电池");
        }
        WarframeItemResolutionDTO resolution = this.itemResolver.resolve(parsed);
        if (resolution.ambiguous()) {
            return CommandResult.rejected("名称存在多个候选，请输入更完整的名称：" + resolution.candidates().stream()
                .map(WarframeItemDTO::getLocalizedName).collect(Collectors.joining("、")));
        }
        if (!resolution.found()) {
            return CommandResult.rejected("没有找到对应的 Warframe Wiki 物品：" + parsed);
        }
        WarframeItemDTO item = resolution.item();
        String wikiSlug = StrUtil.firstNonBlank(item.getWikiSlug(), item.getLocalizedName(), item.getOfficialName());
        if (StrUtil.isBlank(wikiSlug)) {
            return CommandResult.rejected("没有找到对应的 Warframe Wiki 物品：" + parsed);
        }
        String encodedSlug = URLEncoder.encode(wikiSlug, StandardCharsets.UTF_8).replace("+", "%20");
        String displayName = StrUtil.firstNonBlank(item.getLocalizedName(), item.getOfficialName(), parsed);
        return CommandResult.text("Warframe Wiki：" + displayName + "\n" + WIKI_BASE_URL + encodedSlug);
    }

}
