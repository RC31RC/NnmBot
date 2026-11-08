package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface WarframeOfficialFileRepository extends JpaRepository<WarframeOfficialFile, String> {
    /** 对官方记录和人工维护写入共用同一文件行锁。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeOfficialFile> findFirstByProductPath(String productPath);
}
