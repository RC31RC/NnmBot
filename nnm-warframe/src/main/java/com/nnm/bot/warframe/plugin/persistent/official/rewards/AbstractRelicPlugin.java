package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRelicService;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 遗物查询公共层。
 *
 * <p>从官方遗物根关系树读取奖励和地点；同身份自维护整树优先，不跨来源拼接明细。</p>
 */
abstract class AbstractRelicPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.relics"));
    }

    private static final Pattern PAGE_ARGUMENT = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern INVALID_PAGE_ARGUMENT = Pattern.compile("(?:^|\\s)--page(?:\\s|=|$)");

    @Resource
    protected WarframeRelicService relicService;

    @Resource
    private WarframeImageResolver imageResolver;

    /**
     * 查询遗物与奖励的对应资料。
     *
     * @param arguments 当前命令或操作的参数。
     * @return 命令结果对象。
     */
    protected CommandResult relics(String arguments) {
        Query query = parse(arguments);
        if (query == null) {
            return CommandResult.rejected("页码必须为正整数：--page 2");
        }
        if (query.keyword().length() > 200) {
            return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        }
        List<WarframeRelicDetailsDTO> details = relicService.findDetails(query.keyword());
        WarframeRelicVO display = WarframeRelicVO.relics(query.keyword(), details, query.page(),
                relic -> imageResolver.resolve(relic.icon(), relic.sourceCode()));
        return CommandResult.view("warframe.relics", "遗物：" + display.pagination(), display);
    }

    /**
     * 从当前命令调用中提取遗物查询参数文本。
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 参数对应的文本。
     */
    protected String argument(CommandInvocation invocation) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param arguments 当前命令或操作的参数。
     * @return 查询对象。
     */
    private Query parse(String arguments) {
        String value = arguments == null ? "" : arguments.trim();
        Matcher matcher = PAGE_ARGUMENT.matcher(value);
        if (!matcher.find()) {
            return INVALID_PAGE_ARGUMENT.matcher(value).find() ? null : new Query(value, 1);
        }
        int page;
        try {
            page = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException exception) {
            page = Integer.MAX_VALUE;
        }
        String keyword = value.substring(0, matcher.start()).trim();
        return page < 1 || INVALID_PAGE_ARGUMENT.matcher(keyword).find() ? null : new Query(keyword, page);
    }

    private record Query(String keyword, int page) {}
}
