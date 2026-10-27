package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeEquipmentService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractEquipmentPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.equipment"));
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern INVALID_PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s|=|$)");

    @Resource
    protected WarframeEquipmentService equipmentService;

    /**
     * 解析装备查询参数，调用战甲或武器服务并组装命令结果。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param arguments 当前命令或操作的参数。
     * @return 命令结果对象。
     */
    protected CommandResult equipment(Category dataset, String title, String arguments) {
        String query = arguments == null ? "" : arguments.trim();
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
            return CommandResult.rejected("页码必须为正整数：--page 2");
        }
        if (query.length() > 200) {
            return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        }
        WarframeEquipmentVO display = WarframeEquipmentVO.page(query, title, equipmentService.find(dataset, query), page);
        return CommandResult.view("warframe.equipment", title + "：" + display.pagination(), display);
    }
}
