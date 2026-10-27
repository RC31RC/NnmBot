package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import java.util.Optional;

public interface WarframeOfficialFileService {
    /**
     * 按给定条件查询业务数据。
     *
     * @param path 当前文件或业务资源的路径。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeOfficialFile> find(String path);
}
