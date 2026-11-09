package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialConflictBatchDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 只读取仍可能影响准入的状态投影，不装载审核大字段。
     *
     * @param statuses 待审、采纳中和可重试状态。
     * @return 轻量准入候选。
     */
    List<WarframeOfficialConflictBatchDTO> findAllProjectedByStatusIn(Collection<String> statuses);
}
