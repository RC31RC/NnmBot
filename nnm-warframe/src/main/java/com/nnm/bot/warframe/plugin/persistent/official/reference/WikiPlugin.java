package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.RelatedParameter;
import java.util.Map;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeLinkVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 返回物品对应的灰机 Wiki 页面，不抓取 Wiki 内容。 */
@BotPluginCommand(
        code = WarframeCommandCodes.WIKI,
        matchType = MatchType.PREFIX,
        name = "Wiki",
        allowRelatedAction = true,
        gameCode = "warframe",
        order = 20)
// 不抓取 Wiki 内容；仅使用官方名称与人工别名/术语白名单解析页面名。
public class WikiPlugin extends BotPlugin {

    /**
     * 声明百科查询关联动作需要保留的上下文键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public Set<String> actionContextKeys() { return Set.of("item.name", "item.marketSlug"); }

    /**
     * 声明百科关联查询可接受的参数及其展示信息。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<RelatedParameter> relatedParameters() {
        return List.of(new RelatedParameter("item", ParameterType.TEXT, 200));
    }

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.links"));
    }

    private static final String WIKI_BASE_URL = "https://warframe.huijiwiki.com/wiki/";

    @Resource
    private WarframeItemResolverService itemResolver;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (StrUtil.isBlank(invocation.arguments())) {
            return CommandResult.rejected("Wiki 命令格式：wk 物品名称，例如：wk 电池");
        }
        WarframeItemResolutionDTO resolution = this.itemResolver.resolve(invocation.arguments());
        if (resolution.ambiguous()) {
            return CommandResult.rejected("名称存在多个候选，请输入更完整的名称："
                    + resolution.candidates().stream()
                            .map(WarframeItemDTO::getLocalizedName)
                            .collect(Collectors.joining("、")));
        }
        if (!resolution.found()) {
            return CommandResult.rejected("没有找到对应的 Warframe Wiki 物品："
                    + invocation.arguments());
        }
        WarframeItemDTO item = resolution.item();
        String wikiSlug = StrUtil.firstNonBlank(
                item.getWikiSlug(), item.getLocalizedName(), item.getOfficialName());
        if (StrUtil.isBlank(wikiSlug)) {
            return CommandResult.rejected("没有找到对应的 Warframe Wiki 物品："
                    + invocation.arguments());
        }
        String encodedSlug = URLEncoder.encode(
                        wikiSlug, StandardCharsets.UTF_8)
                .replace("+", "%20");
        String displayName = StrUtil.firstNonBlank(
                item.getLocalizedName(), item.getOfficialName(), invocation.arguments());
        return CommandResult.view("warframe.links", "Warframe Wiki",
                new WarframeLinkVO(displayName, WIKI_BASE_URL + encodedSlug))
                .withActionContext(StrUtil.isBlank(item.getMarketSlug()) ? Map.of("item.name", displayName)
                        : Map.of("item.name", displayName, "item.marketSlug", item.getMarketSlug()));
    }

}
