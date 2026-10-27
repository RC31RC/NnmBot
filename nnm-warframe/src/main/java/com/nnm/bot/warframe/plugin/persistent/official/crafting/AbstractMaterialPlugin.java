package com.nnm.bot.warframe.plugin.persistent.official.crafting;

import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeMaterialService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractMaterialPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.materials"));
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");

    @Resource
    protected WarframeMaterialService materialService;

    /**
     * 解析材料查询参数，调用对应服务并组装命令结果。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param arguments 当前命令或操作的参数。
     * @return 命令结果对象。
     */
    protected CommandResult material(Category dataset, String title, String arguments) {
        String query = arguments == null ? "" : arguments.trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                return CommandResult.rejected("页码必须为有效的正整数：--page 2");
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find()) {
            return CommandResult.rejected("仅支持末尾分页参数：--page 2");
        }
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeMaterialVO display = WarframeMaterialVO.page(query, title, materialService.find(dataset, query), page);
        display = new WarframeMaterialVO(display.query(), display.category(), display.page(),
                display.totalPages(), display.totalCount(),
                display.items().stream().map(materialService::withImage).toList(), display.notice());
        return CommandResult.view("warframe.materials", display.text(), display);
    }
}
