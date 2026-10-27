package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportHead;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportHeadDTO;
import java.util.Optional;

public interface WarframeOfficialImportHeadRepository extends JpaRepository<WarframeOfficialImportHead, Integer> {
    /**
     * 读取当前已采纳的官方数据批次标识。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<OfficialImportHeadDTO> findProjectedById(Integer id);

    default Optional<String> findBatchId(Integer id) {
        return findProjectedById(id).map(OfficialImportHeadDTO::getBatchId);
    }

    /**
     * 查询指定主键对应的首条记录。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarframeOfficialImportHead> findFirstById(Integer id);
}
