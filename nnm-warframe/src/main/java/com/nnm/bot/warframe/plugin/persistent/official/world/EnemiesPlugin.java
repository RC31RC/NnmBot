package com.nnm.bot.warframe.plugin.persistent.official.world;

import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeEnemyVO;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;
import com.nnm.bot.warframe.persistent.official.pojo.query.world.WarframeEnemyQuery;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeEnemyService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.ENEMIES, matchType = MatchType.PREFIX)
public final class EnemiesPlugin extends BotPlugin<Void, WarframeEnemyQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 类型化的查询条件及参数错误
     */
    @Override
    protected WarframeEnemyQuery parse(CommandInvocation invocation, Void prepared) {
        String arguments = invocation.arguments() == null ? "" : invocation.arguments().trim();
        EnemySection section = EnemySection.AVATARS;
        int page = 1;
        Set<String> seen = new HashSet<>();
        Matcher matcher = OPTION.matcher(arguments);
        StringBuilder keyword = new StringBuilder();
        int start = 0;
        while (matcher.find()) {
            keyword.append(arguments, start, matcher.start()).append(' ');
            start = matcher.end();
            if (!seen.add(matcher.group(1)))
                return new WarframeEnemyQuery(arguments, section, page, "敌人资料参数重复");
            if ("section".equals(matcher.group(1))) {
                try {
                    section = EnemySection.valueOf(matcher.group(2).replace('-', '_').toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                    return new WarframeEnemyQuery(arguments, section, page,
                        "分区必须为 avatars、agents、damage-controllers、droptables、hit-proxies 或 ai-weapons");
                }
            } else {
                if (!matcher.group(2).matches("[0-9]+"))
                    return new WarframeEnemyQuery(arguments, section, page, "页码必须为正整数");
                try {
                    page = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException exception) {
                    page = Integer.MAX_VALUE;
                }
                if (page < 1)
                    return new WarframeEnemyQuery(arguments, section, page, "页码必须为正整数");
            }
        }
        String query = keyword.append(arguments.substring(start)).toString().trim().replaceAll("\\s+", " ");
        if (UNKNOWN.matcher(query).find())
            return new WarframeEnemyQuery(query, section, page, "敌人资料参数不合法");
        if (query.length() > 500)
            return new WarframeEnemyQuery(query, section, page, "查询内容过长，请缩短到 500 字以内");
        return new WarframeEnemyQuery(query, section, page, null);
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(section|page)(?:\\s+|=)(\\S+)");
    private static final Pattern UNKNOWN = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeEnemyService enemyService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @param parsed 已解析的查询条件。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeEnemyQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeEnemyVO display = enemyService.query(parsed.keyword(), parsed.section(), parsed.page());
        return CommandResult.html(display.text(), display);
    }
}
