package com.nnm.bot.warframe.runtime.service.riven;

import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.core.message.model.BotIncomingAttachment;

/** 紫卡图片输入的下载和 OCR 适配边界。 */
public interface WarframeRivenOcrService {

    /**
     * 识别附件中的紫卡文本，并保留识别过程和失败原因。
     *
     * @param attachment 附件。
     * @return 文字识别文本结果对象。
     */
    OcrTextResult recognizeDetailed(BotIncomingAttachment attachment);

    /**
     * 识别输入中的目标业务对象。
     *
     * @param attachment 附件。
     * @return 识别输入中的目标业务对象对应的文本。
     */
    default String recognize(BotIncomingAttachment attachment) {
        OcrTextResult result = recognizeDetailed(attachment);
        return result == null ? "" : result.text().trim();
    }
}
