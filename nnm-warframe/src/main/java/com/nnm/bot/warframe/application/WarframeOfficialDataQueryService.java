package com.nnm.bot.warframe.application;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.pojo.WarframeOfficialRecordItem;
import com.nnm.bot.warframe.pojo.WarframeOfficialRecordQueryRequest;

public interface WarframeOfficialDataQueryService {

    PageResult<WarframeOfficialRecordItem> query(WarframeOfficialRecordQueryRequest request);
}
