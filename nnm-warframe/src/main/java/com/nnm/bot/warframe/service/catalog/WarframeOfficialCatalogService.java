package com.nnm.bot.warframe.service.catalog;

import com.nnm.bot.warframe.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.pojo.WarframeOfficialDataRecord;
import com.nnm.bot.warframe.pojo.WarframeOfficialRegionData;

import java.util.List;

/**
 * 为命令插件提供官方 PublicExport 的只读结构化数据。
 */
public interface WarframeOfficialCatalogService {

    List<WarframeOfficialDataRecord> records();

    List<WarframeOfficialDataRecord> records(WarframeOfficialDataDomain domain);

    List<WarframeOfficialRegionData> regions();
}
