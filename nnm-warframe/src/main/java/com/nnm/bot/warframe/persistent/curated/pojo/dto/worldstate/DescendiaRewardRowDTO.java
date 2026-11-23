package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRowDTO;

/** 数据库人工维护楼层奖池的非受管读取投影。 */
public interface DescendiaRewardRowDTO extends WarframeCommunityRowDTO {

    Integer getFloorNumber();

    String getDifficulty();

    String getLabel();

    String getOfficialPoolKey();

    String getExpectedPoolName();

    Integer getItemCountOverride();

    String getNote();

    boolean isGuaranteed();

    String getSourceUrl();
}
