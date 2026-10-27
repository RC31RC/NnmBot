package com.nnm.bot.warframe.runtime.service.riven.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.config.ocr.OcrProperties;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.ocr.OcrService;
import com.nnm.bot.common.ocr.OcrTextResult;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenOcrService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/** 紫卡图片 OCR 适配实现；业务字段解析由 WarframeRivenAnalysisService 负责。 */
@Service
public class WarframeRivenOcrServiceImpl implements WarframeRivenOcrService {

    @Resource
    private HttpClient httpClient;

    @Resource
    private OcrService ocrService;
    @Resource
    private OcrProperties ocrProperties;

    /**
     * 识别附件中的紫卡文本，并保留识别过程和失败原因。
     *
     * @param attachment 附件。
     * @return 文字识别文本结果对象。
     */
    @Override
    public OcrTextResult recognizeDetailed(BotIncomingAttachment attachment) {
        if (attachment == null || !attachment.isImage() || !attachment.hasRemoteReference()) {
            return new OcrTextResult("", List.of());
        }
        if (StrUtil.isBlank(attachment.url())) {
            // fileId 需要渠道级文件 API，不能把平台标识拼成 URL。
            return new OcrTextResult("", List.of());
        }
        byte[] image = httpClient.getLimitedBytes(attachment.url(),
                Map.of("Accept", "image/*"), ocrProperties.getMaxInputBytes());
        return ocrService.recognize(image);
    }
}
