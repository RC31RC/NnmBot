package com.nnm.bot.warframe.plugin.persistent.official.reference;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeItemRelationCommandDTO;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemRelationService;
import jakarta.annotation.Resource;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

abstract class AbstractItemRelationPlugin extends BotPlugin<Void, WarframeItemRelationCommandDTO> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeItemRelationCommandDTO parse(CommandInvocation invocation, Void prepared) {
        try {
            return parseQuery(invocation.arguments());
        } catch (BusinessException exception) {
            return new WarframeItemRelationCommandDTO("", null, 1, exception.getMessage());
        }
    }

    private static final Pattern OPTIONS_START = Pattern.compile("(?:^|\\s)--");
    private static final Pattern OPTION = Pattern.compile("^--(type|page)(?:=|\\s+)(\\S+)(?:\\s+|$)");
    @Resource
    protected WarframeItemRelationService relationService;

    /**
     * 解析组件或掉落查询参数，调用关系查询服务并组装命令结果。
     *
     * @param query 已解析的关系检索条件。
     * @param components 组件。
     * @return 命令结果对象。
     */
    protected CommandResult relations(WarframeItemRelationCommandDTO query, boolean components) {
        if (query.error() != null)
            return CommandResult.rejected(query.error());
        WarframeItemRelationVO display = components
            ? WarframeItemRelationVO.componentPage(query.keyword(),
                relationService.components(query.type(), query.keyword()), query.page())
            : WarframeItemRelationVO.dropPage(query.keyword(), relationService.drops(query.type(), query.keyword()),
                query.page());
        if (components) {
            display =
                new WarframeItemRelationVO(display.query(), display.category(), display.page(), display.totalPages(),
                    display.totalCount(), display.components().stream().map(relationService::withImage).toList(),
                    display.drops(), display.notice());
        }
        return CommandResult.html(display.text(), display);
    }

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param arguments 当前命令或操作的参数。
     * @return 查询对象。
     */
    private WarframeItemRelationCommandDTO parseQuery(String arguments) {
        String input = arguments == null ? "" : arguments.trim();
        String keyword = input;
        Map<String, String> options = new HashMap<>();
        Matcher start = OPTIONS_START.matcher(input);
        if (start.find()) {
            keyword = input.substring(0, start.start()).trim();
            String remaining = input.substring(start.end() - 2);
            while (StrUtil.isNotBlank(remaining)) {
                Matcher option = OPTION.matcher(remaining);
                if (!option.find() || options.putIfAbsent(option.group(1), option.group(2)) != null) {
                    throw new BusinessException(CommonErrorCode.PARAM_INVALID, "仅支持末尾 --type 和 --page 参数，不能重复");
                }
                remaining = remaining.substring(option.end()).trim();
            }
        }
        if (keyword.length() > 200)
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "查询内容过长，请缩短到 200 字以内");
        Category type = options.containsKey("type") ? Category.parse(options.get("type")) : null;
        if (type != null && !WarframeCommandCatalog.relationParents().contains(type)) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "物品类型不支持组件或掉落查询");
        }
        int page = 1;
        if (options.containsKey("page")) {
            String value = options.get("page");
            if (!value.matches("[0-9]+") || value.matches("0+")) {
                throw new BusinessException(CommonErrorCode.PARAM_INVALID, "页码必须为有效的正整数");
            }
            try {
                page = Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new BusinessException(CommonErrorCode.PARAM_INVALID, "页码必须为有效的正整数");
            }
        }
        return new WarframeItemRelationCommandDTO(keyword, type, page, null);
    }

}
