package com.nnm.bot.warframe.plugin.persistent.official.world;

import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeVendorDefinitionVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeVendorDefinitionService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.VENDOR_DEFINITIONS, matchType = MatchType.PREFIX,
        name = "商人静态定义", gameCode = "warframe", order = 108)
public final class VendorDefinitionsPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.vendor-definitions"));
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(section|page)(?:\\s+|=)(\\S+)");
    private static final Pattern UNKNOWN = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeVendorDefinitionService definitionService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String arguments = invocation.arguments() == null ? "" : invocation.arguments().trim();
        VendorSection section = VendorSection.ITEMS;
        int page = 1;
        Set<String> seen = new HashSet<>();
        Matcher matcher = OPTION.matcher(arguments);
        StringBuilder keyword = new StringBuilder();
        int start = 0;
        while (matcher.find()) {
            keyword.append(arguments, start, matcher.start()).append(' ');
            start = matcher.end();
            if (!seen.add(matcher.group(1))) return CommandResult.rejected("商人静态定义参数重复");
            if ("section".equals(matcher.group(1))) {
                try {
                    section = VendorSection.valueOf(matcher.group(2).replace('-', '_').toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                    return CommandResult.rejected("分区必须为 items 或 random-prices");
                }
            } else {
                if (!matcher.group(2).matches("[0-9]+")) return CommandResult.rejected("页码必须为正整数");
                try {
                    page = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException exception) {
                    page = Integer.MAX_VALUE;
                }
                if (page < 1) return CommandResult.rejected("页码必须为正整数");
            }
        }
        String query = keyword.append(arguments.substring(start)).toString().trim().replaceAll("\\s+", " ");
        if (UNKNOWN.matcher(query).find()) return CommandResult.rejected("商人静态定义参数不合法");
        if (query.length() > 500) return CommandResult.rejected("查询内容过长，请缩短到 500 字以内");
        WarframeVendorDefinitionVO display = definitionService.query(query, section, page);
        return CommandResult.view("warframe.vendor-definitions", display.text(), display);
    }
}
