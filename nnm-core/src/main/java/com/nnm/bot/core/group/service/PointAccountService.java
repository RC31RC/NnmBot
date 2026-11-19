package com.nnm.bot.core.group.service;

import com.nnm.bot.core.group.pojo.dto.PointBalanceDTO;
import com.nnm.bot.core.group.pojo.dto.PointChangeResultDTO;
import com.nnm.bot.core.group.pojo.dto.PointOperationDTO;
import java.util.Optional;

/** 群组积分账户能力，签到、命令成本和后台调整共享此边界。 */
public interface PointAccountService {

    PointChangeResultDTO earn(PointOperationDTO operation);

    PointChangeResultDTO spend(PointOperationDTO operation);

    PointChangeResultDTO refund(String originalIdempotencyKey, String description);

    PointChangeResultDTO adjust(long groupId, long memberId, long targetPoints, String idempotencyKey,
        String description);

    Optional<PointBalanceDTO> balance(long groupId, long memberId);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
