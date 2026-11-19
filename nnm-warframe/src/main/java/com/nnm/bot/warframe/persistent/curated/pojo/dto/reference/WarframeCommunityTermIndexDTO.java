package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.nnm.bot.warframe.runtime.cache.dataset.DatasetSnapshotMetrics;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityTermGroup;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.TermDTO;
import java.util.List;
import java.util.Map;

/** 社区术语的一份快照同时拥有普通跳板与分组展示索引，避免派生索引跨代复用。 */
public record WarframeCommunityTermIndexDTO(List<WarframeCommunityRecordDTO<TermDTO>> records,
    Map<String, List<String>> targets,
    Map<WarframeCommunityTermGroup, Map<String, List<String>>> displayTerms) implements DatasetSnapshotMetrics {
    /** @return 当前术语表的业务记录数。 */
    @Override
    public long entryCount() {
        return records.size();
    }
}
