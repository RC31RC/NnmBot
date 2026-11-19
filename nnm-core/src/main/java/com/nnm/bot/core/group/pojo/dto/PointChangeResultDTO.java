package com.nnm.bot.core.group.pojo.dto;

/** 积分变更结果，重复幂等请求也返回当前余额。 */
public record PointChangeResultDTO(boolean allowed, boolean applied, PointBalanceDTO balance, Long transactionId,
    String message) {

    public static PointChangeResultDTO denied(String message, PointBalanceDTO balance) {
        return new PointChangeResultDTO(false, false, balance, null, message);
    }

    public static PointChangeResultDTO applied(PointBalanceDTO balance, Long transactionId) {
        return new PointChangeResultDTO(true, true, balance, transactionId, null);
    }

    public static PointChangeResultDTO repeated(PointBalanceDTO balance, Long transactionId) {
        return new PointChangeResultDTO(true, false, balance, transactionId, null);
    }
}
