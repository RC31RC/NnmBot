package com.nnm.bot.warframe.persistent.official.pojo.query;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;

/** 维护状态属于记录属性；domain 只选择具体官方主表。 */
public record OfficialRecordIdentityQuery(
        MaintenanceType maintenanceType, String domain, String bkey) {
}
