package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.equipment.WarframeEquipmentQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeEquipmentService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractEquipmentPlugin extends BotPlugin<Void, WarframeEquipmentQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeEquipmentQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || INVALID_PAGE.matcher(query).find()) {
            return new WarframeEquipmentQuery(query, page, "页码必须为正整数：--page 2");
        }
        if (query.length() > 200) {
            return new WarframeEquipmentQuery(query, page, "查询内容过长，请缩短到 200 字以内");
        }
        return new WarframeEquipmentQuery(query, page, null);
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern INVALID_PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s|=|$)");

    @Resource
    protected WarframeEquipmentService equipmentService;

    /**
     * 解析装备查询参数，调用战甲或武器服务并组装命令结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param query 已解析的装备检索条件。
     * @return 命令结果对象。
     */
    protected CommandResult equipment(Category dataset, String title, WarframeEquipmentQuery query) {
        if (query.error() != null)
            return CommandResult.rejected(query.error());
        WarframeEquipmentVO display = WarframeEquipmentVO.page(query.keyword(), title,
            equipmentService.find(dataset, query.keyword()), query.page());
        return CommandResult.html(title + "：" + display.pagination(), display);
    }
}
