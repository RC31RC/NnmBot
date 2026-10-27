package com.nnm.bot.warframe.persistent.official.pojo.entity.support;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/** 各官方主表复用的记录字段；仅 Java 映射基类，不对应总表。 */
@Getter
@Setter
@MappedSuperclass
public abstract class OfficialRecord extends OfficialRow {

    /** 记录维护状态；不改变官方来源类别。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 20)
    private MaintenanceType maintenanceType = MaintenanceType.OFFICIAL;

    /** 当前独立 JSON 内稳定业务键，与维护类别共同构成记录身份。 */
    @Column(name = "bkey", nullable = false, length = 500)
    private String bkey;

    /** 成品提供的英文展示名；无名称的结构记录为空。 */
    @Column(name = "official_name", length = 500)
    private String officialName;

    /** 成品提供的中文展示名；无中文翻译时为空。 */
    @Column(name = "localized_name", length = 500)
    private String localizedName;

    /** 成品提供的父级展示名；没有父级时为空。 */
    @Column(name = "parent_name", length = 500)
    private String parentName;

    /** 成品提供的产品分类；未分类时为空。 */
    @Column(name = "product_category", length = 160)
    private String productCategory;

    /** 成品的 Codex 排除标志；成品未提供时为空。 */
    @Column(name = "exclude_from_codex")
    private Boolean excludeFromCodex;

    /** 英文短描述；没有描述时为空。 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 英文长描述；成品未提供时为空。 */
    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    /** 本地维护的 Wiki 页面路径；未维护时为空。 */
    @Column(name = "wiki_slug", length = 300)
    private String wikiSlug;

    /** 成品或本地维护的市场 slug；未关联时为空。 */
    @Column(name = "market_slug", length = 300)
    private String marketSlug;

    /** 当前版本产品原始 JSON；保留成品嵌套字段。 */
    @Column(name = "raw_json", nullable = false, columnDefinition = "TEXT")
    private String rawJson;

    /** 当前版本中文产品 JSON；未发布单独中文结构时为空。 */
    @Column(name = "localized_raw_json", columnDefinition = "TEXT")
    private String localizedRawJson;

    /** 是否参与运行时查询；上游删除及手工删除时整树物理删除。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 本地首次导入时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 本地最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 在首次持久化前初始化记录状态和时间。
     */
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        normalize();
    }

    /**
     * 在更新持久化记录前维护更新时间。
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    /** 清理业务检索字段首尾空白，统一可选字段的空值表示。 */
    private void normalize() {
        bkey = StrUtil.trim(bkey);
        officialName = trimToNull(officialName);
        localizedName = trimToNull(localizedName);
        parentName = trimToNull(parentName);
        productCategory = trimToNull(productCategory);
        description = trimToNull(description);
        longDescription = trimToNull(longDescription);
        wikiSlug = trimToNull(wikiSlug);
        marketSlug = trimToNull(marketSlug);
    }

    /**
     * 清理文本；仅含空白时返回空值。
     *
     * @param value 待校验或规范化的文本。
     * @return 裁剪文本首尾空白，并将空白结果统一为缺省值对应的文本。
     */
    private static String trimToNull(String value) {
        String trimmed = StrUtil.trim(value);
        return StrUtil.isBlank(trimmed) ? null : trimmed;
    }
}
