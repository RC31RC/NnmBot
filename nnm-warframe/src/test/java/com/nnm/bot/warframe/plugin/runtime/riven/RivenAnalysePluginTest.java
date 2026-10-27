package com.nnm.bot.warframe.plugin.runtime.riven;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenAnalysisService;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenOcrService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RivenAnalysePluginTest {

    private final WarframeRivenOcrService ocrService = mock(WarframeRivenOcrService.class);
    private final WarframeRivenAnalysisService analysisService =
            mock(WarframeRivenAnalysisService.class);

    @Test
    void textEntryShowsIndependentRanksAndReferenceInsteadOfGrading() {
        WarframeRivenAnalysisVO result = result("拉特昂", 8, 12);
        when(analysisService.analyze("拉特昂")).thenReturn(result);

        CommandResult response = plugin().handle(invocation("拉特昂", List.of()));

        assertThat(response.successful()).isTrue();
        assertThat(CommandReplyTestData.data(response)).isSameAs(result);
        assertThat(CommandReplyTestData.text(response)).contains(
                "卡片等级：8", "紫卡段位需求：12", "官方属性原始参数：", "未收录参数", "负 后坐力");
        assertThat(CommandReplyTestData.text(response)).doesNotContain("武器段位", "评级");
        verifyNoInteractions(ocrService);
    }

    @Test
    void imageEntryUsesDetailedOcrAndDoesNotAnalyzeFallbackText() {
        BotIncomingAttachment image = image();
        OcrTextResult ocr = new OcrTextResult("图片中的文字", List.of("图片中的文字"));
        WarframeRivenAnalysisVO result = result(ocr.text(), null, null);
        when(ocrService.recognizeDetailed(image)).thenReturn(ocr);
        when(analysisService.analyze(ocr)).thenReturn(result);

        CommandResult response = plugin().handle(invocation("备用文本", List.of(image)));

        assertThat(CommandReplyTestData.data(response)).isSameAs(result);
        assertThat(CommandReplyTestData.text(response)).contains("卡片等级：未确认", "紫卡段位需求：未确认");
        verify(analysisService).analyze(ocr);
    }

    @Test
    void emptyImageRecognitionIsRejected() {
        BotIncomingAttachment image = image();
        OcrTextResult ocr = new OcrTextResult("", List.of());
        when(ocrService.recognizeDetailed(image)).thenReturn(ocr);
        when(analysisService.analyze(ocr)).thenReturn(result("", null, null));

        CommandResult response = plugin().handle(invocation("", List.of(image)));

        assertThat(response.successful()).isFalse();
        assertThat(CommandReplyTestData.text(response)).contains("OCR 未识别出文字");
    }

    @Test
    void emptyInputIsRejectedBeforeCallingServices() {
        assertThat(plugin().handle(invocation("", List.of())).successful()).isFalse();
        verifyNoInteractions(analysisService, ocrService);
    }

    @Test
    void referenceRangeIsExplicitlySeparatedFromLegalityOrValueRating() {
        WarframeRivenAnalysisVO old = result("拉特昂", 8, 12);
        WarframeRivenAnalysisVO result = new WarframeRivenAnalysisVO(old.rawText(), old.rawLines(), old.weaponName(),
                old.weaponType(), old.weaponCandidates(), true, 8, 12, old.attributes(), false,
                old.trends(), old.lineAnalyses(), old.missingFields(), List.of(
                new WarframeRivenAnalysisVO.ReferenceRangeVO("recoil", "后坐力", "percent",
                        40.0, 60.0, 0.0, "IN_RANGE", "", "FIXED_1_2_V1",
                        "/weapon", "weapon-source", "trend-source"),
                new WarframeRivenAnalysisVO.ReferenceRangeVO("multishot", "多重", "percent",
                        null, null, null, "UNAVAILABLE", "cardRankUnconfirmed", "FIXED_1_2_V1",
                        "/weapon", "", "")));
        when(analysisService.analyze("拉特昂")).thenReturn(result);
        CommandResult response = plugin().handle(invocation("拉特昂", List.of()));
        assertThat(CommandReplyTestData.text(response)).contains("Nyx 旧版规则，仅供核对", "40.00 ~ 60.00%",
                "偏差 +0.00%", "参考范围内", "待确认 [cardRankUnconfirmed]")
                .doesNotContain("评级", "合法");
        assertThat(CommandReplyTestData.data(response)).isSameAs(result);
    }

    private RivenAnalysePlugin plugin() {
        RivenAnalysePlugin plugin = new RivenAnalysePlugin();
        ReflectionTestUtils.setField(plugin, "rivenOcrService", ocrService);
        ReflectionTestUtils.setField(plugin, "rivenAnalysisService", analysisService);
        return plugin;
    }

    private CommandInvocation invocation(String text, List<BotIncomingAttachment> attachments) {
        BotMessageContext context = mock(BotMessageContext.class);
        when(context.attachments()).thenReturn(attachments);
        return new CommandInvocation(context, "riven-analyse", "紫卡分析", text);
    }

    private BotIncomingAttachment image() {
        return new BotIncomingAttachment(BotIncomingAttachmentType.IMAGE,
                "riven.png", "image/png", "http://localhost/riven.png", null,
                null, null, null, Map.of());
    }

    private WarframeRivenAnalysisVO result(String raw, Integer rank, Integer mastery) {
        return new WarframeRivenAnalysisVO(raw, List.of(raw), "拉特昂", "WEAPON",
                List.of("拉特昂"), true, rank, mastery, List.of(
                new WarframeRivenAnalysisVO.AttributeVO(
                        "recoil", "后坐力", 50.0, true, false, raw, "OCR_ONLY")),
                false, List.of(new WarframeRivenAnalysisVO.TrendVO("后坐力", "", null)),
                List.of(), List.of());
    }
}
