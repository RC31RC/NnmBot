package com.nnm.bot.core.message.exception;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;

/**
 * 命令图片渲染失败。
 *
 * <p>这是响应层唯一允许转换为文字回退的渲染异常。网络、发送和业务异常
 * 不应包装成此异常，否则会掩盖真实故障。</p>
 */
public class BotImageRenderException extends BusinessException {

    public BotImageRenderException(String detail, Throwable cause) {
        super(BotChannelErrorCode.IMAGE_RENDER_FAILED,
                "命令图片渲染失败: " + detail, cause);
    }
}
