package com.nnm.bot.warframe.persistent.official.pojo.query;

import org.springframework.data.domain.Pageable;

public record OfficialRecordQuery(String domain, String keyword, String maintenanceType, Pageable pageable) {
}
