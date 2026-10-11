package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.ocr.OcrService;
import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.warframe.application.WarframeRivenOcrService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/** 紫卡图片 OCR 适配实现；业务字段解析由 WarframeRivenAnalysisService 负责。 */
@Service
public class WarframeRivenOcrServiceImpl implements WarframeRivenOcrService {

    @Resource
    private HttpClient httpClient;

    @Resource
    private OcrService ocrService;

    @Override
    public OcrTextResult recognizeDetailed(BotIncomingAttachment attachment) {
        if (attachment == null || !attachment.isImage() || !attachment.hasRemoteReference()) {
            return new OcrTextResult("", java.util.List.of());
        }
        if (attachment.url() == null || attachment.url().isBlank()) {
            // fileId 需要渠道级文件 API，不能把平台标识拼成 URL。
            return new OcrTextResult("", java.util.List.of());
        }
        HttpClient.ByteResult response = httpClient.getBytes(
                attachment.url(), Map.of("Accept", "image/*"));
        if (response == null || !response.isSuccess()
                || response.body() == null || response.body().length == 0) {
            return new OcrTextResult("", java.util.List.of());
        }
        OcrTextResult result = ocrService.recognize(response.body());
        return result == null ? new OcrTextResult("", java.util.List.of()) : result;
    }
}
