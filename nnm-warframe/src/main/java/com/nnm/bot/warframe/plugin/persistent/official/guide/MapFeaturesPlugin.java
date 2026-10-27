package com.nnm.bot.warframe.plugin.persistent.official.guide;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FeaturePropertiesVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FeatureVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.GeometryVO;
import com.nnm.bot.warframe.persistent.official.service.guide.WarframeGuideService;
import jakarta.annotation.Resource;
import java.util.List;

@BotPluginCommand(code = WarframeCommandCodes.MAP_FEATURES, matchType = MatchType.PREFIX,
        name = "开放世界地图标注", gameCode = "warframe", order = 75)
public final class MapFeaturesPlugin extends AbstractGuidePlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.guide"));
    }

    @Resource
    private WarframeGuideService guideService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        Query query;
        try {
            query = query(invocation.arguments(), true);
        } catch (IllegalArgumentException exception) {
            return CommandResult.rejected(exception.getMessage());
        }
        WarframeGuideVO<FeatureVO> display = guideService.map(query.keyword(), query.world(), query.layer(), query.page());
        StringBuilder text = new StringBuilder("开放世界地图标注：").append(display.pagination())
                .append("\n坐标为来源地图图片坐标，不是经纬度");
        for (FeatureVO feature : display.items()) {
            text.append("\n\n").append(feature.name() == null ? "未命名标注" : feature.name())
                    .append(" · ").append(WarframeGuideVO.worldName(feature.world()))
                    .append("\n图层：").append(feature.layer());
            GeometryVO geometry = feature.geometry();
            if (geometry == null) {
                text.append("\n位置未提供");
            } else if ("Point".equals(geometry.type()) && geometry.pointCoordinates().size() >= 2) {
                text.append("\n图片坐标：").append(geometry.pointCoordinates().get(0).toPlainString())
                        .append(" / ").append(geometry.pointCoordinates().get(1).toPlainString());
            } else if (geometry.boundaryRingCount() > 0) {
                text.append("\n区域：").append(geometry.boundaryRingCount()).append(" 个边界环");
            }
            FeaturePropertiesVO properties = feature.properties();
            if (properties != null && StrUtil.isNotBlank(properties.description())) {
                text.append("\n").append(properties.description());
            }
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的地图标注");
        return CommandResult.view("warframe.guide", text.toString(), display);
    }
}
