package com.nnm.bot.admin.repository.bot.notification;

import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.constant.bot.notification.BotNotificationConstants.JobStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 按状态、执行时间和所有者维护预约通知任务。 */
public interface BotNotificationJobRepository extends JpaRepository<BotNotificationJob, UUID> {
    List<BotNotificationJob> findTop100ByOwnerIdOrderByCreatedAtDesc(long ownerId);

    List<BotNotificationJob> findTop50ByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(JobStatus status,
        Instant now);

    List<BotNotificationJob> findTop50ByStatusInAndUpdatedAtBefore(Collection<JobStatus> statuses, Instant cutoff);

    long countByOwnerIdAndStatusIn(long ownerId, Collection<JobStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select job from BotNotificationJob job where job.id = :id")
    Optional<BotNotificationJob> lockById(@Param("id") UUID id);
}
