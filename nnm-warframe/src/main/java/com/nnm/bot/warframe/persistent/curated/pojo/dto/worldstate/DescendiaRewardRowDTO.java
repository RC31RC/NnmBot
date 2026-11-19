package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

/** 数据库人工维护楼层奖池的非受管读取投影。 */
public interface DescendiaRewardRowDTO {
    String getRecordKey();

    Integer getFloorNumber();

    String getDifficulty();

    String getLabel();

    String getOfficialPoolKey();

    String getExpectedPoolName();

    String getSourceRewardKey();

    Integer getItemCountOverride();

    String getOptionsJson();

    String getNote();

    boolean isGuaranteed();

    String getSourceUrl();
}
