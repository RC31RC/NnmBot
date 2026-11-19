package com.nnm.bot.core.group.pojo.dto;

/** 群组成员积分账户的只读余额快照。 */
public record PointBalanceDTO(long points, long totalEarned, long totalSpent) {
}
