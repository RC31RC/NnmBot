package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;

/** warframe.vendor-definitions 命令的类型化查询条件。 */
public record WarframeVendorDefinitionCommandDTO(String keyword, VendorSection section, int page, String error) {
}
