package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialRefreshStaging;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialRefreshStagingRepository
    extends JpaRepository<WarframeOfficialRefreshStaging, String> {
    /**
     * 查询超过暂存 TTL 的上传账本。
     *
     * @param cutoff 创建时间截止值。
     * @return 过期上传账本。
     */
    List<WarframeOfficialRefreshStaging> findAllByCreatedAtBefore(Instant cutoff);
}
