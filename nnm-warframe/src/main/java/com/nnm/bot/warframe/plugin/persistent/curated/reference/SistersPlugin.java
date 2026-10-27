package com.nnm.bot.warframe.plugin.persistent.curated.reference;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.TermDTO;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeTermCommandVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;

/** 查询信条相关数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SISTERS,
        matchType = MatchType.PREFIX,
        name = "信条",
        gameCode = "warframe",
        order = 67)
public final class SistersPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.community"));
    }

    @Resource
    private WarframeCommunityCatalogService catalog;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        List<WarframeCommunityRecordDTO<TermDTO>> items = catalog.terms().stream()
                .filter(item -> "sisters".equalsIgnoreCase(item.details().termGroup()))
                .filter(item -> contains(item.details().englishText(), invocation.arguments())
                        || contains(item.details().chineseText(), invocation.arguments()))
                .limit(20).toList();
        return CommandResult.view("warframe.community", CollUtil.isEmpty(items) ? "没有找到信条数据" : "信条：找到 " + items.size() + " 条",
                new WarframeTermCommandVO(invocation.arguments(), items.stream()
                        .map(item -> WarframeTermCommandVO.TermVO.from(item.details())).toList(),
                        "当前为信条静态资料，不是实时武器出售清单"));
    }

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param query 当前业务的查询条件。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private static boolean contains(String value, String query) {
        return StrUtil.isBlank(query) || (value != null
                && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }
}
