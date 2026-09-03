package com.nnm.bot.warframe.application;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordItem;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordQueryRequest;

public interface WarframeOfficialDataQueryService {

    PageResult<WarframeOfficialRecordItem> query(WarframeOfficialRecordQueryRequest request);
}
