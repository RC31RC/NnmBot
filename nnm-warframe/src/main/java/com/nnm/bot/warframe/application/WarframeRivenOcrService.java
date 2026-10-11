package com.nnm.bot.warframe.application;

import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.common.ocr.OcrTextResult;

/** 紫卡图片输入的下载和 OCR 适配边界。 */
public interface WarframeRivenOcrService {

    /**
     * 读取图片附件并保留 OCR 行，供业务分析服务处理。
     */
    OcrTextResult recognizeDetailed(BotIncomingAttachment attachment);

    default String recognize(BotIncomingAttachment attachment) {
        OcrTextResult result = recognizeDetailed(attachment);
        return result == null ? "" : result.text().trim();
    }
}
