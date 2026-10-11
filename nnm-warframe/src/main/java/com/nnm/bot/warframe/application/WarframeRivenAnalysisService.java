package com.nnm.bot.warframe.application;

import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.warframe.pojo.WarframeRivenAnalysisResult;

/** 将紫卡 OCR 或文本转换为可审计的结构化分析结果。 */
public interface WarframeRivenAnalysisService {

    WarframeRivenAnalysisResult analyze(OcrTextResult ocr);

    default WarframeRivenAnalysisResult analyze(String text) {
        return analyze(new OcrTextResult(text, java.util.List.of()));
    }
}
