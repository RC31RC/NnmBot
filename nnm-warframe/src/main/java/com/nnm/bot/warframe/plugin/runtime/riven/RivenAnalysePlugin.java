package com.nnm.bot.warframe.plugin.runtime.riven;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.message.service.MessageInputService;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.runtime.pojo.dto.riven.WarframeRivenInputDTO;
import com.nnm.bot.warframe.runtime.constant.WarframeRivenErrorCode;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenAnalysisService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenOcrService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** 分析紫卡截图或文本。 */
@BotPluginCommand(code = WarframeCommandCodes.RIVEN_ANALYSE, matchType = MatchType.PREFIX)
public final class RivenAnalysePlugin extends BotPlugin<WarframeRivenInputDTO, WarframeRivenInputDTO> {

    /** 平台图片/引用读取由 core 适配，不依赖具体 SDK。 */
    @Resource
    private MessageInputService messageInputs;
    /** 图片识别入口；不在插件中重复实现下载与 OCR。 */
    @Resource
    private WarframeRivenOcrService rivenOcrService;
    /** 图片与文本共用的结构化分析服务。 */
    @Resource
    private WarframeRivenAnalysisService rivenAnalysisService;

    /**
     * 优先分析消息图片，没有图片时使用命令正文。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 归一化文字或 OCR 识别结果；输入缺失或不可读时抛业务异常。
     */
    @Override
    public WarframeRivenInputDTO prepare(CommandInvocation invocation) {
        Optional<BotIncomingAttachment> image = messageInputs.firstImage(invocation);
        if (image.isPresent()) {
            var recognition = rivenOcrService.recognizeDetailed(image.get());
            if (StrUtil.isBlank(recognition.text())) {
                throw new BusinessException(WarframeRivenErrorCode.INPUT_UNREADABLE);
            }
            return new WarframeRivenInputDTO(null, recognition);
        }
        if (StrUtil.isBlank(invocation.arguments())) {
            throw new BusinessException(WarframeRivenErrorCode.INPUT_REQUIRED);
        }
        return new WarframeRivenInputDTO(invocation.arguments().trim(), null);
    }

    /**
     * 将事务外完成的图片识别或文字准备结果转为本次的类型化输入。
     *
     * @param invocation 当前调用上下文。
     * @param prepared 已准备的文字或识别结果。
     * @return 用于处理业务的输入。
     */
    @Override
    protected WarframeRivenInputDTO parse(CommandInvocation invocation, WarframeRivenInputDTO prepared) {
        return prepared;
    }

    /**
     * 使用已准备的文本在命令事务内分析，不再下载图片或等待 OCR。
     *
     * @param invocation 当前命令参数
     * @param prepared 机器人消息的准备输入；结构化 DTO 调用时为 null
     * @return 紫卡分析展示结果
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, WarframeRivenInputDTO prepared,
        WarframeRivenInputDTO parsed) {
        WarframeRivenAnalysisVO result = parsed.recognition() == null ? rivenAnalysisService.analyze(parsed.text())
            : rivenAnalysisService.analyze(parsed.recognition());
        return CommandResult.html(format(result), result);
    }

    /**
     * 原始证据与参考项目区间分开展示，不把范围比较描述为现行合法性或品质评级。
     *
     * @param result 当前正在组装的业务结果。
     * @return 将业务数据格式化为展示文本对应的文本。
     */
    private static String format(WarframeRivenAnalysisVO result) {
        StringBuilder text = new StringBuilder("紫卡结构化分析");
        text.append("\n武器：").append(StrUtil.isBlank(result.weaponName()) ? "未确认" : result.weaponName());
        if (CollUtil.isNotEmpty(result.weaponCandidates())) {
            text.append("\n武器候选：").append(String.join("、", result.weaponCandidates()));
        }
        if (StrUtil.isNotBlank(result.weaponType())) {
            text.append("\n类型：").append(result.weaponType());
        }
        text.append("\n卡片等级：").append(result.rank() == null ? "未确认" : result.rank());
        text.append("\n紫卡段位需求：").append(result.masteryRank() == null ? "未确认" : result.masteryRank());
        text.append("\n词条：");
        if (CollUtil.isEmpty(result.attributes()) && !result.negativeAny()) {
            text.append("未确认");
        } else {
            result.attributes()
                .forEach(attribute -> text.append("\n").append(attribute.negative() ? "负 " : "正 ")
                    .append(attribute.displayName()).append(" [").append(attribute.code()).append("]")
                    .append(attribute.value() == null ? "" : " = " + attribute.value()));
            if (result.negativeAny())
                text.append("\n负面：任意");
        }
        if (CollUtil.isNotEmpty(result.trends())) {
            text.append("\n官方属性原始参数：");
            result.trends().forEach(trend -> text.append("\n").append(trend.displayName()).append("（")
                .append(CollUtil.isEmpty(trend.parameters()) ? "未收录参数" : trend.parameters()).append("）"));
        }
        if (CollUtil.isNotEmpty(result.referenceRanges())) {
            text.append("\n社区参考范围（Nyx 旧版规则，仅供核对）：");
            result.referenceRanges().forEach(range -> {
                text.append("\n").append(range.displayName()).append("：");
                if ("UNAVAILABLE".equals(range.status())) {
                    text.append("待确认 [").append(range.reason()).append("]");
                } else {
                    String unit = switch (range.units()) {
                        case "percent", "%" -> "%";
                        case "seconds" -> "秒";
                        default -> "";
                    };
                    text.append(String.format(Locale.ROOT, "%.2f ~ %.2f%s，偏差 %+.2f%%", range.lower(), range.upper(),
                        unit, range.deviationPercent()))
                        .append("IN_RANGE".equals(range.status()) ? "（参考范围内）" : "（参考范围外）");
                }
            });
        }
        if (CollUtil.isNotEmpty(result.missingFields())) {
            text.append("\n待确认：").append(String.join("、", result.missingFields()));
        }
        return text.toString();
    }
}
