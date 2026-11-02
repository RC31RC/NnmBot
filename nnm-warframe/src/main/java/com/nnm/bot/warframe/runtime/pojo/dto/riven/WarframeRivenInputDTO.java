package com.nnm.bot.warframe.runtime.pojo.dto.riven;

import com.nnm.bot.common.ocr.OcrTextResult;

/**
 * 紫卡命令在事务外取得的文本输入，原始识别证据保留给统一分析服务。
 *
 * @param text 直接输入的文字
 * @param recognition OCR 输出；纯文字输入时为空
 */
public record WarframeRivenInputDTO(String text, OcrTextResult recognition) {
}
