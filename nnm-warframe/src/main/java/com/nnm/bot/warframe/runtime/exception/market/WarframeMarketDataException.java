package com.nnm.bot.warframe.runtime.exception.market;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.MarketErrorCode;

/** 市场上游响应违反类型合同，保留内部原文诊断，对外返回稳定提示。 */
public final class WarframeMarketDataException extends SystemException {
    /**
     * 创建上游协议系统异常。
     *
     * @param message 内部诊断，不直接返回给用户
     * @param cause 原始解析异常；缺少字段时可为 null
     */
    public WarframeMarketDataException(String message, Throwable cause) {
        super(MarketErrorCode.RESPONSE_INVALID, message, cause);
    }
}
