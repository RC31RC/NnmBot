package com.nnm.bot.warframe.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Warframe 统一解析模型。
 *
 * <p>这是跨官方、社区和人工数据源的运行时模型，不是数据库实体。
 * 三类数据必须在持久化层保持隔离，解析器只在这里合并它们对插件暴露的字段。</p>
 */
@Getter
@Setter
public class WarframeItem {

    private Long id;

    private String sourceKey;
    private String exportType;
    private String recordType;
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

    /**
     * 仅为尚未迁移的测试夹具和外部调用方保留，不参与持久化，也不参与新的业务判断。
     */
    private String sourceType;
}
