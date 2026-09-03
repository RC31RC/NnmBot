package com.nnm.bot.common.ocr;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

/**
 * OCR 识别结果。
 *
 * @param text 合并后的识别文本
 * @param lines 按行拆分的识别结果
 */
public record OcrTextResult(String text, List<String> lines) {

    public OcrTextResult {
        text = ObjectUtil.isNull(text) ? "" : text;
        lines = CollUtil.isEmpty(lines) ? List.of() : List.copyOf(lines);
    }
}
