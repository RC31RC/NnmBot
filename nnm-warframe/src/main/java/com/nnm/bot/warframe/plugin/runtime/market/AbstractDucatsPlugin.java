package com.nnm.bot.warframe.plugin.runtime.market;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.warframe.constant.WarframeCommandInputConstants;
import com.nnm.bot.warframe.persistent.official.constant.OfficialCommandErrorCode;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.api.CommandInvocation;
import com.nnm.bot.core.plugin.api.CommandResult;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketDucatsService;
import jakarta.annotation.Resource;
import java.util.regex.Pattern;

abstract class AbstractDucatsPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : CharSequenceUtil.trim(invocation.arguments());
    }

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
        String keyword = arguments == null ? "" : CharSequenceUtil.trim(arguments);
        int page = 1;
        Matcher matcher = PAGE.matcher(keyword);
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            keyword = CharSequenceUtil.trim(keyword.substring(0, matcher.start()));
        }
        if (page < 1 || INVALID_PAGE.matcher(keyword).find()) {
            return CommandResult.rejected("页码必须为正整数：--page 2");
        }
        if (keyword.length() > WarframeCommandInputConstants.MAX_QUERY_LENGTH)
            return CommandResult.rejected(OfficialCommandErrorCode.QUERY_TOO_LONG.getMessage());
        String title = gold ? "金垃圾" : "银垃圾";
        return ducatsService.ranking(gold, keyword, page)
            .map(value -> CommandResult.text(title + "\n" + value.summary()))
            .orElseGet(() -> CommandResult.text("市场成交统计暂时不可用，请稍后重试"));
    }
}
