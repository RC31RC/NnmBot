package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.application.WarframeRivenOcrService;
import com.nnm.bot.warframe.application.WarframeRivenAnalysisService;
import com.nnm.bot.warframe.pojo.WarframeRivenAnalysisResult;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import jakarta.annotation.Resource;

import java.util.Optional;

/** 分析紫卡截图或文本。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_ANALYSE,
        matchType = MatchType.PREFIX,
        name = "紫卡分析",
        gameCode = "warframe",
        order = 73)
public final class RivenAnalysePlugin extends BotPlugin {

    @Resource
    private WarframeRivenOcrService rivenOcrService;
    @Resource
    private WarframeRivenAnalysisService rivenAnalysisService;

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        Optional<BotIncomingAttachment> image = invocation.context().attachments().stream()
                .filter(BotIncomingAttachment::isImage)
                .findFirst();
        if (image.isPresent()) {
            var result = rivenAnalysisService.analyze(
                    rivenOcrService.recognizeDetailed(image.get()));
            return result.rawText().isBlank()
                    ? CommandResult.rejected("紫卡图片无法下载或 OCR 未识别出文字")
                    : CommandResult.of(format(result), result);
        }
        String text = invocation.arguments() == null ? "" : invocation.arguments().trim();
        if (text.isBlank()) {
            return CommandResult.rejected("紫卡分析需要图片或完整紫卡文本输入");
        }
        WarframeRivenAnalysisResult result = rivenAnalysisService.analyze(text);
        return CommandResult.of(format(result), result);
    }

    private static String format(WarframeRivenAnalysisResult result) {
        StringBuilder text = new StringBuilder("紫卡结构化分析");
        text.append("\n武器：").append(result.weaponName().isBlank()
                ? "未确认" : result.weaponName());
        if (!result.weaponCandidates().isEmpty()) {
            text.append("\n武器候选：").append(String.join("、", result.weaponCandidates()));
        }
        if (!result.weaponType().isBlank()) {
            text.append("\n类型：").append(result.weaponType());
        }
        text.append("\n卡片等级：").append(result.rank() == null ? "未确认" : result.rank());
        text.append("\n武器段位：").append(
                result.masteryRank() == null ? "未确认" : result.masteryRank());
        text.append("\n词条：");
        if (result.attributes().isEmpty() && !result.negativeAny()) {
            text.append("未确认");
        } else {
            result.attributes().forEach(attribute -> text.append("\n")
                    .append(attribute.negative() ? "负 " : "正 ")
                    .append(attribute.displayName())
                    .append(" [").append(attribute.code()).append("]")
                    .append(attribute.value() == null ? "" : " = " + attribute.value()));
            if (result.negativeAny()) text.append("\n负面：任意");
        }
        if (!result.trends().isEmpty()) {
            text.append("\n趋势：");
            result.trends().forEach(trend -> text.append("\n")
                    .append(trend.displayName()).append("（")
                    .append(trend.coefficient() == null ? "无系数" : trend.coefficient())
                    .append("）"));
        }
        if (!result.missingFields().isEmpty()) {
            text.append("\n待确认：").append(String.join("、", result.missingFields()));
        }
        return text.toString();
    }
}
