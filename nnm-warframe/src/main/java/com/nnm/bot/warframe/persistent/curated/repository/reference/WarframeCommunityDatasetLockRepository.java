package com.nnm.bot.warframe.persistent.curated.repository.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityDatasetLock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface WarframeCommunityDatasetLockRepository extends JpaRepository<WarframeCommunityDatasetLock, String> {
    /**
     * 读取指定社区资料类型的互斥行，供采纳事务加锁使用。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeCommunityDatasetLock> findByDataset(String dataset);
}
