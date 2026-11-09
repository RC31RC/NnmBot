package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.nnm.bot.warframe.config.cache.DatasetSnapshotMetrics;
import java.util.List;
import java.util.Map;

/** 社区表自身的记录与名称跳板，不缓存关联后的官方身份。 */
public record WarframeCommunityNameIndexDTO<T>(List<WarframeCommunityRecordDTO<T>> records,
    Map<String, List<String>> targets) implements DatasetSnapshotMetrics {
    /** @return 本数据集快照拥有的业务记录数。 */
    @Override
    public long entryCount() {
        return records.size();
    }

}
