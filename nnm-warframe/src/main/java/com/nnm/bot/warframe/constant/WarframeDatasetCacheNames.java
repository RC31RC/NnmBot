package com.nnm.bot.warframe.constant;

import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;

/** 来源隔离的缓存命名；子表始终跟随所属 JSON 的版本。 */
public final class WarframeDatasetCacheNames {
    public static final String SNAPSHOTS = "_snapshots";
    public static final String RECORDS = "_records";
    public static final String NAMES = "names";
    public static final String REFERENCES = "references";
    public static final String CATALOG = "catalog";
    public static final String IMAGES = "images";

    private WarframeDatasetCacheNames() {}

    /**
     * 取得官方独立数据集的缓存命名空间。
     *
     * @param domain 独立 JSON 领域。
     * @return 与社区数据隔离的命名空间。
     */
    public static String official(WarframeOfficialDataDomain domain) {
        return "official:" + domain.productPath();
    }

    /**
     * 取得社区独立数据集的缓存命名空间。
     *
     * @param dataset 社区白名单数据集。
     * @return 与官方数据隔离的命名空间。
     */
    public static String community(WarframeCommunityDataset dataset) {
        return "community:" + dataset.code();
    }

    /**
     * 将数据集身份转换为 cache2k 允许的区域名，版本通知仍使用原始身份。
     *
     * @param dataset 来源隔离的数据集身份。
     * @param suffix 快照或按键记录用途。
     * @return 稳定的缓存区域名。
     */
    public static String region(String dataset, String suffix) {
        return "warframe_" + dataset.replaceAll("[^a-zA-Z0-9_]", "_") + suffix;
    }
}
