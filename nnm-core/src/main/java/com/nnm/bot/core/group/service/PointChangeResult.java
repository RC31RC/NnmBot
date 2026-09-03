package com.nnm.bot.core.group.service;

/** 积分变更结果，重复幂等请求也返回当前余额。 */
public record PointChangeResult(boolean allowed,
                                boolean applied,
                                PointBalance balance,
                                Long transactionId,
                                String message) {

    public static PointChangeResult denied(String message, PointBalance balance) {
        return new PointChangeResult(false, false, balance, null, message);
    }

    public static PointChangeResult applied(PointBalance balance, Long transactionId) {
        return new PointChangeResult(true, true, balance, transactionId, null);
    }

    public static PointChangeResult repeated(PointBalance balance, Long transactionId) {
        return new PointChangeResult(true, false, balance, transactionId, null);
    }
}
