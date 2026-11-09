package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface WarframeOfficialFileRepository extends JpaRepository<WarframeOfficialFile, String> {
    /** 对官方记录和人工维护写入共用同一文件行锁。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeOfficialFile> findFirstByProductPath(String productPath);

    /**
     * 一次只读取版本列；补偿遗漏通知时不扫描业务表。
     *
     * @return 各独立数据集的已提交版本。
     */
    @Query("select new com.nnm.bot.warframe.config.cache.DatasetRevisionDTO(concat('official:', f.productPath), f.dataRevision) from WarframeOfficialFile f")
    List<DatasetRevisionDTO> findDataRevisions();
}
