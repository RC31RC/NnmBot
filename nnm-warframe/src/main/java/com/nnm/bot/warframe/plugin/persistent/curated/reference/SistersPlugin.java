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
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeTermCommandVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;

/** 查询信条相关数据。 */
@BotPluginCommand(code = WarframeCommandCodes.SISTERS, matchType = MatchType.PREFIX)
public final class SistersPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }

    @Resource
    private WarframeCommunityCatalogService catalog;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        List<WarframeCommunityRecordDTO<TermDTO>> items =
            catalog.terms().stream().filter(item -> "sisters".equalsIgnoreCase(item.details().termGroup()))
                .filter(item -> contains(item.details().englishText(), parsed)
                    || contains(item.details().chineseText(), parsed))
                .limit(20).toList();
        return CommandResult.html(CollUtil.isEmpty(items) ? "没有找到信条数据" : "信条：找到 " + items.size() + " 条",
            new WarframeTermCommandVO(parsed,
                items.stream().map(item -> WarframeTermCommandVO.TermVO.from(item.details())).toList(),
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
        return StrUtil.isBlank(query)
            || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }
}
