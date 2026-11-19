package com.nnm.bot.common.ocr;

/** 将图像输入转换为识别文本的 OCR 能力端口。 */
public interface OcrService {

    /** 识别图像中的文字。
     *
     * @param image 编码后的图像字节
     * @return 合并文本及逐行识别结果
     */
    OcrTextResult recognize(byte[] image);
}
