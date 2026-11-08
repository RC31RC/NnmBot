package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface WarframeOfficialImportBatchRepository extends JpaRepository<WarframeOfficialImportBatch, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from WarframeOfficialImportBatch b where b.id = :id")
    Optional<WarframeOfficialImportBatch> lockById(@Param("id") String id);

    List<WarframeOfficialImportBatch> findAllByStatusAndApplyPlanJsonIsNullOrderByCreatedAtAsc(String status);

    /**
     * 查询是否仍有某种持久化批次状态。
     *
     * @param status 批次状态。
     * @return 是否有符合状态的批次。
     */
    boolean existsByStatus(String status);

    /**
     * 查询仍在运行的审核批次供后台显示。
     *
     * @param status 批次状态。
     * @return 按创建时间倒序排列的审核批次。
     */
    List<WarframeOfficialImportBatch> findAllByStatusOrderByCreatedAtDesc(String status);

    /**
     * 查询超过心跳 TTL 的采纳批次，恢复时还会再次复核锁与时间。
     *
     * @param status 批次状态。
     * @param cutoff 最后心跳截止时间。
     * @return 可能中断的采纳批次。
     */
    List<WarframeOfficialImportBatch> findAllByStatusAndApplyingAtBefore(String status, Instant cutoff);

    List<WarframeOfficialImportBatch> findAllByStatusAndExpiresAtAfterOrderByCreatedAtDesc(String status, Instant now);
}
