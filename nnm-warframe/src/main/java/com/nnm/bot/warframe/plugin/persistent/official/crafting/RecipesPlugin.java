package com.nnm.bot.warframe.plugin.persistent.official.crafting;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO.EntryVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.crafting.WarframeRecipeQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeRecipeService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.RECIPES, matchType = MatchType.PREFIX)
// Plus ExportRecipes/ExportDojoRecipes -> recipe + ingredient，同文件完整候选取数。
public final class RecipesPlugin extends BotPlugin<Void, WarframeRecipeQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeRecipeQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        String kind = null;
        HashSet<String> options = new HashSet<String>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find())
                break;
            if (!options.add(matcher.group(1)))
                return new WarframeRecipeQuery(query, kind, page, "参数不能重复");
            String value = matcher.group(2);
            if ("kind".equals(matcher.group(1))) {
                if (!"ordinary".equals(value) && !"dojo".equals(value)) {
                    return new WarframeRecipeQuery(query, kind, page, "配方类型必须为 ordinary 或 dojo");
                }
                kind = value;
            } else {
                if (!value.matches("[0-9]+"))
                    return new WarframeRecipeQuery(query, kind, page, "页码必须为正整数");
                try {
                    page = Integer.parseInt(value);
                } catch (NumberFormatException exception) {
                    page = Integer.MAX_VALUE;
                }
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || UNKNOWN_OPTION.matcher(query).find())
            return new WarframeRecipeQuery(query, kind, page, "配方参数不合法");
        if (query.length() > 200)
            return new WarframeRecipeQuery(query, kind, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeRecipeQuery(query, kind, page, null);
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|kind)(?:\\s+|=)(\\S+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeRecipeService recipeService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeRecipeQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        String query = parsed.keyword();
        int page = parsed.page();
        String kind = parsed.kind();
        WarframeRecipeVO display = recipeService.page(query, kind, page);
        StringBuilder text = new StringBuilder("制作配方：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.name()).append(" · ")
                .append("dojo".equals(item.recipeKind()) ? "道场" : "普通制作");
            text.append("\n产物：").append(item.productName());
            if (item.resultCount() != null)
                text.append(" × ").append(item.resultCount());
            if (item.buildPrice() != null)
                text.append("\n信用点：").append(item.buildPrice());
            if (item.buildTime() != null)
                text.append("\n制作时间：").append(item.buildTime()).append(" 秒");
            if (item.skipTimePrice() != null)
                text.append("\n加速费用：").append(item.skipTimePrice());
            if (item.replicatePrice() != null)
                text.append("\n蓝图复制费用：").append(item.replicatePrice());
            if ("source_clan_scale".equals(item.quantityBasis()))
                text.append("\n材料口径：原导出氏族规模");
            if (CollUtil.isEmpty(item.ingredients()))
                text.append("\n材料明细未提供或未采纳");
            else
                item.ingredients().forEach(
                    material -> text.append("\n").append(material.name()).append(" × ").append(material.count()));
        }
        if (CollUtil.isEmpty(display.items()))
            text.append("\n没有找到匹配的配方");
        return CommandResult.html(text.toString(), display);
    }
}
