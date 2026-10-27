package com.nnm.bot.warframe.plugin.runtime.market;

import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketDucatsService;
import jakarta.annotation.Resource;
import java.util.regex.Pattern;

abstract class AbstractDucatsPlugin extends BotPlugin {
    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern INVALID_PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s|=|$)");

    @Resource
    private WarframeMarketDucatsService ducatsService;

    /**
     * 按业务规则生成成交统计排行。
     *
     * @param gold 金档。
     * @param arguments 当前命令或操作的参数。
     * @return 命令结果对象。
     */
    protected CommandResult ranking(boolean gold, String arguments) {
        String keyword = arguments == null ? "" : arguments.trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(keyword);
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            keyword = keyword.substring(0, matcher.start()).trim();
        }
        if (page < 1 || INVALID_PAGE.matcher(keyword).find()) {
            return CommandResult.rejected("页码必须为正整数：--page 2");
        }
        if (keyword.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        String title = gold ? "金垃圾" : "银垃圾";
        return ducatsService.ranking(gold, keyword, page)
                .map(value -> CommandResult.text(title + "\n" + value.summary()))
                .orElseGet(() -> CommandResult.text("市场成交统计暂时不可用，请稍后重试"));
    }
}
