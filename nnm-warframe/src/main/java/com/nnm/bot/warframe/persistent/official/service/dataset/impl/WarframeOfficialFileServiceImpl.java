package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialFileService;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Optional;

/** 按 Nnm JSON 完整路径读取逐文件版本状态。 */
@Service
public class WarframeOfficialFileServiceImpl implements WarframeOfficialFileService {
    @Resource
    private WarframeOfficialFileRepository files;

    /**
     * 按给定条件查询业务数据。
     *
     * @param path 当前文件或业务资源的路径。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    public Optional<WarframeOfficialFile> find(String path) {
        if (WarframeOfficialDataDomain.fromPath(path) == null) return Optional.empty();
        return files.findById(path).filter(file -> file.getAdoptedSha256() != null);
    }
}
