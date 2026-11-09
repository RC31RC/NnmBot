package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO.EntryVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeUpgradeCommandDTO;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeUpgradeService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

abstract class AbstractUpgradePlugin extends BotPlugin<Void, WarframeUpgradeCommandDTO> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeUpgradeCommandDTO parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Integer rank = null;
        Set<String> options = new HashSet<>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find())
                break;
            if (!options.add(matcher.group(1)))
                return new WarframeUpgradeCommandDTO(query, rank, page, "参数不能重复");
            int value;
            try {
                value = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                if (matcher.group(1).equals("rank"))
                    return new WarframeUpgradeCommandDTO(query, rank, page, "等级超出范围");
                value = Integer.MAX_VALUE;
            }
            if (matcher.group(1).equals("page"))
                page = value;
            else
                rank = value;
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || UNKNOWN_OPTION.matcher(query).find()) {
            return new WarframeUpgradeCommandDTO(query, rank, page, "页码必须为正整数，等级必须为非负整数；套装件数至少为 1");
        }
        if (query.length() > 200)
            return new WarframeUpgradeCommandDTO(query, rank, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeUpgradeCommandDTO(query, rank, page, null);
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|rank)(?:\\s+|=)([0-9]+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    protected WarframeUpgradeService upgradeService;

    /**
     * 解析升级或振幅晶体查询参数，调用对应服务并组装命令结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param query 已解析的升级资料检索条件。
     * @return 命令结果对象。
     */
    protected CommandResult upgrade(Category dataset, String title, WarframeUpgradeCommandDTO query) {
        if (query.error() != null)
            return CommandResult.rejected(query.error());
        if (dataset == Category.MOD_SET && query.rank() != null && query.rank() < 1)
            return CommandResult.rejected("页码必须为正整数，等级必须为非负整数；套装件数至少为 1");
        WarframeUpgradeVO display = WarframeUpgradeVO.page(query.keyword(), title,
            upgradeService.find(dataset, query.keyword(), query.rank()), query.page());
        StringBuilder text = new StringBuilder(title).append("：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.name());
            if (item.rank() != null)
                text.append(" · ").append(item.rank()).append(item.rankUnit());
            if (CollUtil.isEmpty(item.effects()))
                text.append("\n等级效果未提供");
            else
                item.effects().forEach(effect -> text.append("\n").append(effect));
        }
        if (CollUtil.isEmpty(display.items()))
            text.append("\n没有找到匹配的记录");
        return CommandResult.html(text.toString(), display);
    }
}
