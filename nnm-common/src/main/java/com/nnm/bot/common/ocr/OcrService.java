package com.nnm.bot.common.ocr;

public interface OcrService {

    OcrTextResult recognize(byte[] image);
}
