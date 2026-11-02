package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 紫卡命令输入失败，可通过重新提供文字或图片解决。 */
@Getter
@RequiredArgsConstructor
public enum WarframeRivenErrorCode implements ErrorCode {
    INPUT_REQUIRED(422, "紫卡分析需要图片或完整紫卡文本输入", HttpStatus.UNPROCESSABLE_ENTITY),
    INPUT_UNREADABLE(422, "紫卡图片无法下载或 OCR 未识别出文字", HttpStatus.UNPROCESSABLE_ENTITY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
