package com.nnm.bot.warframe.application;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.dto.WarframeItemAliasItem;
import com.nnm.bot.warframe.dto.WarframeItemAliasSaveRequest;
import com.nnm.bot.warframe.dto.WarframeItemItem;

public interface WarframeDataManagementService {

    PageResult<WarframeItemItem> items(String keyword, String sourceType, int current, int size);

    PageResult<WarframeItemAliasItem> aliases(
            String keyword, Long itemId, String sourceType, int current, int size);

    long createAlias(WarframeItemAliasSaveRequest request);

    void updateAlias(long id, WarframeItemAliasSaveRequest request);

    void deleteAlias(long id);
}
