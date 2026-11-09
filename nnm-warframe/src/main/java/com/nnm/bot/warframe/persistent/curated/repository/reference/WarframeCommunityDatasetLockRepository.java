package com.nnm.bot.warframe.persistent.curated.repository.reference;

import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityDatasetLock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface WarframeCommunityDatasetLockRepository extends JpaRepository<WarframeCommunityDatasetLock, String> {
    /**
     * 读取指定社区资料类型的互斥行，供采纳事务加锁使用。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeCommunityDatasetLock> findByDataset(String dataset);

    /**
     * 一次只读取版本列；补偿遗漏通知时不扫描业务表。
     *
     * @return 各独立数据集的已提交版本。
     */
    @Query("select new com.nnm.bot.warframe.config.cache.DatasetRevisionDTO(concat('community:', f.dataset), f.dataRevision) from WarframeCommunityDatasetLock f")
    List<DatasetRevisionDTO> findDataRevisions();
}
