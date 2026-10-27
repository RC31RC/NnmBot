package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/** 官方记录查询投影；不属于 Hibernate 持久化上下文。 */
@Getter
@Setter
public class WarframeOfficialRecordDTO {
    private Long id;

    private MaintenanceType maintenanceType;

    private String bkey;

    private String productPath;

    private String domainCode;

    private String officialName;

    private String localizedName;

    private String parentName;

    private String productCategory;

    private Boolean excludeFromCodex;

    private String description;

    private String longDescription;

    private String wikiSlug;

    private String marketSlug;

    private String rawJson;

    private String localizedRawJson;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
