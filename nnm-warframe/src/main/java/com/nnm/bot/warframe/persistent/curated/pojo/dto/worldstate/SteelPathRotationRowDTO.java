package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

import java.time.LocalDate;

/** 已审核轮换规则的非受管数据库读取投影。 */
public interface SteelPathRotationRowDTO {
    Integer getRotationIndex();

    LocalDate getAnchorWeek();

    String getStoreItem();
}
