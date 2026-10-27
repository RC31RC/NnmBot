package com.nnm.bot.warframe.runtime.service.riven;

import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenAnalysisVO;
import java.util.List;

/** 将紫卡 OCR 或文本转换为可审计的结构化分析结果。 */
public interface WarframeRivenAnalysisService {

    /**
     * 从 OCR 原文及逐行证据解析，不将未知字段推断为确定结论。
     *
     * @param ocr 文字识别。
     * @return 紫卡识别、属性分析与参考数值的展示结果。
     */
    WarframeRivenAnalysisVO analyze(OcrTextResult ocr);

    /**
     * 文本命令与图片 OCR 共用相同解析规则。
     *
     * @param text 待处理的文本。
     * @return 紫卡识别、属性分析与参考数值的展示结果。
     */
    default WarframeRivenAnalysisVO analyze(String text) {
        return analyze(new OcrTextResult(text, List.of()));
    }
}
