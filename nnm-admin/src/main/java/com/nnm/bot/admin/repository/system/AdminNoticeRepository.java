package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.AdminNotice;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, String> {
    List<AdminNotice> findTop100ByRecipientIdAndExpiresAtAfterOrderByCreatedAtDesc(Long recipientId, Instant now);

    List<AdminNotice> findByRecipientIdAndExpiresAtAfterOrderByCreatedAtDesc(Long recipientId, Instant now);

    List<AdminNotice> findByRecipientIdAndExpiresAtAfter(Long recipientId, Instant now);

    long deleteByIdAndRecipientId(String id, Long recipientId);

    long deleteByRecipientId(Long recipientId);

    long deleteByExpiresAtBefore(Instant now);

    boolean existsBySourceKey(String sourceKey);
}
