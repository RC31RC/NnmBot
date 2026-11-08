package com.nnm.bot.warframe.plugin.persistent.official.items;

import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeBundleVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.items.WarframeItemPageQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.service.items.WarframeBundleService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractBundlePlugin extends BotPlugin<Void, WarframeItemPageQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeItemPageQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            if (!matcher.group(1).matches("[0-9]+"))
                return new WarframeItemPageQuery(query, page, "页码必须为正整数");
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find())
            return new WarframeItemPageQuery(query, page, "礼包参数不合法");
        if (query.length() > 200)
            return new WarframeItemPageQuery(query, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeItemPageQuery(query, page, null);
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)(\\S+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    private final Category dataset;
    @Resource
    private WarframeBundleService bundleService;

    /**
     * 构造基础礼包插件，并保存初始化参数。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     */
    protected AbstractBundlePlugin(Category dataset) {
        this.dataset = dataset;
    }

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeItemPageQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeBundleVO display = bundleService.page(dataset, parsed.keyword(), parsed.page());
        return CommandResult.html(display.text(), display);
    }
}
