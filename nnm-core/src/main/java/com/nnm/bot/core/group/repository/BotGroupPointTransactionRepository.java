package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotGroupPointTransactionRepository
        extends JpaRepository<BotGroupPointTransaction, Long> {

    Optional<BotGroupPointTransaction> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByMemberId(Long memberId);
}
