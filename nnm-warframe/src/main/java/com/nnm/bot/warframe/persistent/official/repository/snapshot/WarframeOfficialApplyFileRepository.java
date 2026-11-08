package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialApplyFile;
import jakarta.persistence.LockModeType;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 数据库工作项认领；行锁保证同一文件只被一个实例执行。 */
public interface WarframeOfficialApplyFileRepository extends JpaRepository<WarframeOfficialApplyFile, UUID> {
    // 候选列表不加锁：LIMIT 1 加 SKIP LOCKED 在部分数据库上会跳过第一个已锁项后返回空集。
    @Query("""
        select f.id from WarframeOfficialApplyFile f
        where f.batchId in (select b.id from WarframeOfficialImportBatch b
            where b.status = :applying and b.applyPlanJson is not null)
        and ((f.status = :ready and (f.leaseUntil is null or f.leaseUntil <= :now))
          or (f.status = :running and f.leaseUntil <= :now))
        order by f.updatedAt, f.id
        """)
    List<UUID> findClaimableIds(@Param("applying") String applying, @Param("ready") String ready,
        @Param("running") String running, @Param("now") Instant now, Pageable page);

    /** 单行跳锁再复核状态，不占用同批次的其他工作项。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout",
        value = OfficialDataConstants.APPLY_SKIP_LOCKED_HINT))
    @Query("select f from WarframeOfficialApplyFile f where f.id = :id")
    Optional<WarframeOfficialApplyFile> tryLockById(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from WarframeOfficialApplyFile f where f.id = :id")
    Optional<WarframeOfficialApplyFile> lockById(@Param("id") UUID id);

    List<WarframeOfficialApplyFile> findAllByBatchId(String batchId);

    boolean existsByBatchIdAndStatus(String batchId, String status);
}
