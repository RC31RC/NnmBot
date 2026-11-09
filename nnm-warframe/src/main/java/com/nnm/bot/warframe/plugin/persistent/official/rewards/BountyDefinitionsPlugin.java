package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.dto.catalog.WarframeCatalogPageCommandDTO;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.BOUNTY_DEFINITIONS, matchType = MatchType.PREFIX)
public final class BountyDefinitionsPlugin extends BotPlugin<Void, WarframeCatalogPageCommandDTO> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeCatalogPageCommandDTO parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            if (!matcher.group(1).matches("[0-9]+"))
                return new WarframeCatalogPageCommandDTO(query, page, "页码必须为正整数");
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find())
            return new WarframeCatalogPageCommandDTO(query, page, "赏金定义参数不合法");
        if (query.length() > 200)
            return new WarframeCatalogPageCommandDTO(query, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeCatalogPageCommandDTO(query, page, null);
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)(\\S+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeBountyDefinitionService bountyDefinitionService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeCatalogPageCommandDTO parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeBountyVO display = bountyDefinitionService.page(parsed.keyword(), parsed.page());
        return CommandResult.html(display.text(), display);
    }
}
