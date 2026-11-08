package com.nnm.bot.warframe.plugin.persistent.official.guide;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.guide.WarframeGuideQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

abstract class AbstractGuidePlugin extends BotPlugin<Void, WarframeGuideQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeGuideQuery parse(CommandInvocation invocation, Void prepared) {
        try {
            return query(invocation.arguments(), supportsLayers());
        } catch (BusinessException exception) {
            return new WarframeGuideQuery("", null, null, 1, exception.getMessage());
        }
    }

    /** 地图命令额外接受图层参数。 */
    protected abstract boolean supportsLayers();

    private static final Pattern OPTIONS_START = Pattern.compile("(?:^|\\s)--");
    private static final Pattern OPTION = Pattern.compile("^--([a-z]+)(?:=|\\s+)(\\S+)(?:\\s+|$)");

    /**
     * 执行当前业务域的查询并组织结果。
     *
     * @param arguments 当前命令或操作的参数。
     * @param map 映射。
     * @return 查询对象。
     */
    protected WarframeGuideQuery query(String arguments, boolean map) {
        String input = arguments == null ? "" : arguments.trim();
        Matcher start = OPTIONS_START.matcher(input);
        String keyword = input;
        Map<String, String> options = new HashMap<>();
        if (start.find()) {
            keyword = input.substring(0, start.start()).trim();
            String remaining = input.substring(start.end() - 2);
            while (StrUtil.isNotBlank(remaining)) {
                Matcher matcher = OPTION.matcher(remaining);
                if (!matcher.find())
                    throw new BusinessException(CommonErrorCode.PARAM_INVALID, "攻略参数不合法");
                String key = matcher.group(1);
                if (!Set.of("world", "layer", "page").contains(key) || !map && key.equals("layer")
                    || options.putIfAbsent(key, matcher.group(2)) != null) {
                    throw new BusinessException(CommonErrorCode.PARAM_INVALID, "攻略参数不合法或重复");
                }
                remaining = remaining.substring(matcher.end()).trim();
            }
        }
        if (keyword.length() > 200)
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "查询内容过长，请缩短到 200 字以内");
        String world = options.get("world");
        if (world != null && !Set.of("plains", "vallis", "deimos").contains(world)) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "开放世界参数不合法");
        }
        String layer = options.get("layer");
        if (layer != null && !layer.matches("[a-z][a-z0-9-]{0,79}")) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "地图图层参数不合法");
        }
        int page = 1;
        String pageText = options.get("page");
        if (pageText != null) {
            if (!pageText.matches("[0-9]+") || pageText.matches("0+")) {
                throw new BusinessException(CommonErrorCode.PARAM_INVALID, "页码必须为正整数");
            }
            try {
                page = Integer.parseInt(pageText);
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
        }
        return new WarframeGuideQuery(keyword, world, layer, page, null);
    }

}
