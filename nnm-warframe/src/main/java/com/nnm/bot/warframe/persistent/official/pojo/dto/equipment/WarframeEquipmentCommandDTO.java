package com.nnm.bot.warframe.persistent.official.pojo.dto.equipment;

/** 装备目录命令的检索词、页码及语法校验结果。 */
public record WarframeEquipmentCommandDTO(String keyword, int page, String error) {
}
