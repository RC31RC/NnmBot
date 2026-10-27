package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BotGroupPointTransactionRepository
        extends JpaRepository<BotGroupPointTransaction, Long> {

    Optional<BotGroupPointTransaction> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByMemberId(Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update BotGroupPointTransaction t set t.memberId = :targetMemberId "
            + "where t.groupId = :groupId and t.memberId = :sourceMemberId")
    int updateMemberId(@Param("groupId") Long groupId,
                       @Param("sourceMemberId") Long sourceMemberId,
                       @Param("targetMemberId") Long targetMemberId);
}
