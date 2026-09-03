package com.nnm.bot.warframe.constant;

import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * PublicExport 中可独立查询的文件类型。
 *
 * <p>目前只有高频领域有详情表，其余类型仍从公共导出记录表读取完整 JSON，
 * 但查询边界必须按 exportType 隔离，不能再次合并到 OTHER。</p>
 */
public enum WarframeOfficialDataDomain {
    WEAPON("ExportWeapons"),
    WARFRAME("ExportWarframes"),
    UPGRADE("ExportUpgrades"),
    RELIC_ARCANE("ExportRelicArcane"),
    REGION("ExportRegions"),
    RECIPE("ExportRecipes"),
    CUSTOMS("ExportCustoms"),
    DRONES("ExportDrones"),
    FLAVOUR("ExportFlavour"),
    FUSION_BUNDLES("ExportFusionBundles"),
    GEAR("ExportGear"),
    KEYS("ExportKeys"),
    RESOURCES("ExportResources"),
    SENTINELS("ExportSentinels"),
    SORTIE_REWARDS("ExportSortieRewards"),
    OTHER("");

    private final String exportType;

    WarframeOfficialDataDomain(String exportType) {
        this.exportType = exportType;
    }

    public String exportType() {
        return exportType;
    }

    public static WarframeOfficialDataDomain fromExportType(String exportType) {
        for (WarframeOfficialDataDomain domain : values()) {
            if (domain != OTHER && StrUtil.equalsIgnoreCase(domain.exportType, exportType)) {
                return domain;
            }
        }
        return OTHER;
    }

    public static List<String> knownExportTypes() {
        return List.of(values()).stream()
                .filter(domain -> domain != OTHER)
                .map(WarframeOfficialDataDomain::exportType)
                .toList();
    }
}
