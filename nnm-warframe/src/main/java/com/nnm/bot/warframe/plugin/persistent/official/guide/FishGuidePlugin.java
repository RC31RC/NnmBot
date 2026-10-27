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
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FishVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.FishTimeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.SizeRewardVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.ResourceAmountVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO.TimeConditionVO;
import com.nnm.bot.warframe.persistent.official.service.guide.WarframeGuideService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@BotPluginCommand(code = WarframeCommandCodes.FISH_GUIDE, matchType = MatchType.PREFIX,
        name = "鱼类攻略", gameCode = "warframe", order = 74)
public final class FishGuidePlugin extends AbstractGuidePlugin {

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
            query = query(invocation.arguments(), false);
        } catch (IllegalArgumentException exception) {
            return CommandResult.rejected(exception.getMessage());
        }
        WarframeGuideVO<FishVO> display = guideService.fish(query.keyword(), query.world(), query.page());
        StringBuilder text = new StringBuilder("鱼类攻略：").append(display.pagination());
        for (FishVO fish : display.items()) {
            text.append("\n\n").append(fish.name()).append(" · ").append(WarframeGuideVO.worldName(fish.world()))
                    .append("\n地点：").append(value(fish.location())).append(" · 稀有度：").append(value(fish.rarity()))
                    .append("\n时间：").append(time(fish.time())).append("\n鱼饵：").append(value(fish.baitName()))
                    .append(" · 鱼叉：").append(join(fish.spears(), "未提供"));
            if (fish.maximumMass() != null) text.append("\n最大重量：").append(fish.maximumMass());
            if (fish.maximumPoint() != null) text.append("\n最大评分：").append(fish.maximumPoint());
            text.append("\n独有材料：").append(join(fish.uniqueResources(), "未提供"));
            for (SizeRewardVO reward : fish.sizeRewards()) {
                text.append("\n").append(sizeName(reward.size())).append("：")
                        .append(resources(reward.resources())).append(" · 声望：")
                        .append(reward.standing() == null ? "未提供" : reward.standing().stripTrailingZeros().toPlainString());
            }
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的鱼类攻略");
        return CommandResult.view("warframe.guide", text.toString(), display);
    }

    /**
     * 将输入时间转换为当前业务使用的时间表示。
     *
     * @param time 时间。
     * @return 将输入时间转换为当前业务使用的时间表示对应的文本。
     */
    private static String time(FishTimeVO time) {
        if (time == null) return "未提供";
        String label = value(time.label());
        List<String> active = time.conditions().stream().filter(TimeConditionVO::appears)
                .map(condition -> condition.name() + (condition.preferred() ? "（偏好）" : "")).toList();
        return CollUtil.isEmpty(active) ? label : label + " / " + String.join("、", active);
    }

    /**
     * 将鱼类可产出的资源及数量拼成命令文本。
     * @param resources 资源集合。
     * @return 资源对应的文本。
     */
    private static String resources(List<ResourceAmountVO> resources) {
        if (CollUtil.isEmpty(resources)) return "无材料";
        return resources.stream().map(resource -> resource.name() + " × "
                + (resource.amount() == null ? "未提供" : resource.amount().stripTrailingZeros().toPlainString()))
                .collect(Collectors.joining("、"));
    }

    /**
     * 将鱼类尺寸代码转换为中文名称。
     * @param size 当前操作允许的数据数量。
     * @return 数量名称对应的文本。
     */
    private static String sizeName(String size) {
        return switch (size) {
            case "small" -> "小型";
            case "medium" -> "中型";
            case "large" -> "大型";
            default -> "未分类奖励";
        };
    }

    /**
     * 将缺省显示值转换为命令可读的占位文本。
     * @param value 待校验或规范化的文本。
     * @return 当前值的业务表示对应的文本。
     */
    private static String value(String value) {
        return StrUtil.isBlank(value) ? "未提供" : value;
    }

    /**
     * 按显示顺序拼接多个文本，空集合使用指定提示。
     * @param values 待处理的值集合。
     * @param fallback 缺省。
     * @return 合并对应的文本。
     */
    private static String join(List<String> values, String fallback) {
        return CollUtil.isEmpty(values) ? fallback : String.join("、", values);
    }
}
