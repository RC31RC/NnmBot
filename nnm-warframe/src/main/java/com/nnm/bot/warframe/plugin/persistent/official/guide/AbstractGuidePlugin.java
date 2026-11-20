package com.nnm.bot.warframe.plugin.persistent.official.guide;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.warframe.constant.WarframeCommandInputConstants;
import com.nnm.bot.warframe.persistent.official.constant.OfficialCommandErrorCode;
import com.nnm.bot.common.error.BusinessException;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.dto.guide.WarframeGuideCommandDTO;
import com.nnm.bot.core.plugin.api.CommandInvocation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

abstract class AbstractGuidePlugin extends BotPlugin<Void, WarframeGuideCommandDTO> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeGuideCommandDTO parse(CommandInvocation invocation, Void prepared) {
        try {
            return query(invocation.arguments(), supportsLayers());
        } catch (BusinessException exception) {
            return new WarframeGuideCommandDTO("", null, null, 1, exception.getMessage());
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
    protected WarframeGuideCommandDTO query(String arguments, boolean map) {
        String input = arguments == null ? "" : CharSequenceUtil.trim(arguments);
        Matcher start = OPTIONS_START.matcher(input);
        String keyword = input;
        Map<String, String> options = new HashMap<>();
        if (start.find()) {
            keyword = CharSequenceUtil.trim(input.substring(0, start.start()));
            String remaining = input.substring(start.end() - 2);
            while (CharSequenceUtil.isNotBlank(remaining)) {
                Matcher matcher = OPTION.matcher(remaining);
                if (!matcher.find())
                    throw new BusinessException(OfficialCommandErrorCode.GUIDE_OPTION_INVALID);
                String key = matcher.group(1);
                if (!Set.of("world", "layer", "page").contains(key) || !map && key.equals("layer")
                    || options.putIfAbsent(key, matcher.group(2)) != null) {
                    throw new BusinessException(OfficialCommandErrorCode.GUIDE_OPTION_DUPLICATE);
                }
                remaining = CharSequenceUtil.trim(remaining.substring(matcher.end()));
            }
        }
        if (keyword.length() > WarframeCommandInputConstants.MAX_QUERY_LENGTH)
            throw new BusinessException(OfficialCommandErrorCode.QUERY_TOO_LONG);
        String world = options.get("world");
        if (world != null && !Set.of("plains", "vallis", "deimos").contains(world)) {
            throw new BusinessException(OfficialCommandErrorCode.WORLD_INVALID);
        }
        String layer = options.get("layer");
        if (layer != null && !layer.matches("[a-z][a-z0-9-]{0,79}")) {
            throw new BusinessException(OfficialCommandErrorCode.MAP_LAYER_INVALID);
        }
        int page = 1;
        String pageText = options.get("page");
        if (pageText != null) {
            if (!pageText.matches("[0-9]+") || pageText.matches("0+")) {
                throw new BusinessException(OfficialCommandErrorCode.GUIDE_PAGE_INVALID);
            }
            try {
                page = Integer.parseInt(pageText);
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
        }
        return new WarframeGuideCommandDTO(keyword, world, layer, page, null);
    }

}
