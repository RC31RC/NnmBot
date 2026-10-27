package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO.EntryVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeUpgradeService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

abstract class AbstractUpgradePlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.upgrades"));
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|rank)(?:\\s+|=)([0-9]+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    protected WarframeUpgradeService upgradeService;

    /**
     * 解析升级或振幅晶体查询参数，调用对应服务并组装命令结果。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param arguments 当前命令或操作的参数。
     * @return 命令结果对象。
     */
    protected CommandResult upgrade(Category dataset, String title, String arguments) {
        String query = arguments == null ? "" : arguments.trim();
        int page = 1;
        Integer rank = null;
        Set<String> options = new HashSet<>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find()) break;
            if (!options.add(matcher.group(1))) return CommandResult.rejected("参数不能重复");
            int value;
            try {
                value = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                if (matcher.group(1).equals("rank")) return CommandResult.rejected("等级超出范围");
                value = Integer.MAX_VALUE;
            }
            if (matcher.group(1).equals("page")) page = value;
            else rank = value;
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || UNKNOWN_OPTION.matcher(query).find()
                || dataset == Category.MOD_SET && rank != null && rank < 1) {
            return CommandResult.rejected("页码必须为正整数，等级必须为非负整数；套装件数至少为 1");
        }
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeUpgradeVO display = WarframeUpgradeVO.page(query, title, upgradeService.find(dataset, query, rank), page);
        StringBuilder text = new StringBuilder(title).append("：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.name());
            if (item.rank() != null) text.append(" · ").append(item.rank()).append(item.rankUnit());
            if (CollUtil.isEmpty(item.effects())) text.append("\n等级效果未提供");
            else item.effects().forEach(effect -> text.append("\n").append(effect));
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的记录");
        return CommandResult.view("warframe.upgrades", text.toString(), display);
    }
}
