package com.nnm.bot.core.group.service;

/** 群组成员积分账户的只读余额快照。 */
public record PointBalance(long points, long totalEarned, long totalSpent) {
}
