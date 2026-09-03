package com.nnm.bot.core.group.service;

import java.util.Optional;

/** 群组积分账户能力，签到、命令成本和后台调整共享此边界。 */
public interface PointAccountService {

    PointChangeResult earn(PointOperation operation);

    PointChangeResult spend(PointOperation operation);

    PointChangeResult refund(String originalIdempotencyKey, String description);

    PointChangeResult adjust(long groupId,
                             long memberId,
                             long targetPoints,
                             String idempotencyKey,
                             String description);

    Optional<PointBalance> balance(long groupId, long memberId);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
