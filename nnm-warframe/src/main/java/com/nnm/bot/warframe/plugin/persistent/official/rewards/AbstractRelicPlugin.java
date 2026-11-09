package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicCommandDTO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRelicService;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 遗物查询公共层。
 *
 * <p>
 * 从官方遗物根关系树读取奖励和地点；同身份自维护整树优先，不跨来源拼接明细。
 * </p>
 */
abstract class AbstractRelicPlugin extends BotPlugin<Void, WarframeRelicCommandDTO> {

    /**
     * 将尾部分页参数解析成遗物检索条件。
     *
     * @param invocation 当前命令调用。
     * @param prepared 普通命令不需要准备输入。
     * @return 查询对象；非法分页时为 null。
     */
    @Override
    protected WarframeRelicCommandDTO parse(CommandInvocation invocation, Void prepared) {
        return parseQuery(invocation.arguments());
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
    protected CommandResult relics(WarframeRelicCommandDTO query) {
        if (query == null) {
            return CommandResult.rejected("页码必须为正整数：--page 2");
        }
        if (query.keyword().length() > 200) {
            return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        }
        List<WarframeRelicDetailsDTO> details = relicService.findDetails(query.keyword());
        WarframeRelicVO display = WarframeRelicVO.relics(query.keyword(), details, query.page(),
            relic -> imageResolver.resolve(relic.icon(), relic.sourceCode()));
        return CommandResult.html("遗物：" + display.pagination(), display);
    }

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param arguments 当前命令或操作的参数。
     * @return 查询对象。
     */
    private WarframeRelicCommandDTO parseQuery(String arguments) {
        String value = arguments == null ? "" : arguments.trim();
        Matcher matcher = PAGE_ARGUMENT.matcher(value);
        if (!matcher.find()) {
            return INVALID_PAGE_ARGUMENT.matcher(value).find() ? null : new WarframeRelicCommandDTO(value, 1);
        }
        int page;
        try {
            page = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException exception) {
            page = Integer.MAX_VALUE;
        }
        String keyword = value.substring(0, matcher.start()).trim();
        return page < 1 || INVALID_PAGE_ARGUMENT.matcher(keyword).find() ? null
            : new WarframeRelicCommandDTO(keyword, page);
    }

}
