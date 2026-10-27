package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/** 已选定官方模型记录的运行时搜索视图；维护状态已在目录读取时处理。 */
@Getter
@Setter
public class WarframeItemDTO {

    private Long id;

    private String sourceKey;
    private String domainCode;
    private String officialName;
    private String localizedName;
    private String parentName;
    private String productCategory;
    private String description;
    private String longDescription;
    private String wikiSlug;
    private String marketSlug;
    private String sourceVersion;
    private boolean enabled = true;
    private String rawJson;
    private String localizedRawJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
