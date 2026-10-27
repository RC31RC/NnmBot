package com.nnm.bot.warframe.persistent.official.repository.maintenance;

import java.util.Optional;
import com.nnm.bot.warframe.persistent.official.pojo.entity.maintenance.WarframeOfficialRebuildMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Collection;

public interface WarframeOfficialRebuildMarkerRepository
        extends JpaRepository<WarframeOfficialRebuildMarker, Long> {

    /**
     * 读取指定官方文件的全部待重建标记。
     *
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeOfficialRebuildMarker> findAllByProductPath(String productPath);

    /**
     * 清除指定官方文件集合的待重建标记。
     *
     * @param productPaths 文件产品路径集合。
     */
    void deleteAllByProductPathIn(Collection<String> productPaths);

    /**
     * 按官方文件路径、业务领域及稳定业务键读取待重建标记。
     *
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param domainCode 业务领域代码。
     * @param bkey 记录的稳定业务键；子表中表示直接父记录的主键。
     * @return 可选结果对象。
     */
    Optional<WarframeOfficialRebuildMarker>
            findByProductPathAndDomainCodeAndBkey(String productPath, String domainCode, String bkey);
}
