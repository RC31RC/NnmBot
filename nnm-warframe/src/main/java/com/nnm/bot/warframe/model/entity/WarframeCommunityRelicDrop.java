package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 社区遗物掉落位置数据。
 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_relic_drop")
public class WarframeCommunityRelicDrop extends WarframeCommunityDatasetRecord {
    @Column(name = "relic_name", nullable = false, length = 200)
    private String relicName;

    @Column(name = "relic_record_key", length = 500)
    private String relicRecordKey;

    @Column(name = "location_text", length = 700)
    private String locationText;

    @Column(length = 32)
    private String rarity;

    @Column(precision = 12, scale = 4)
    private BigDecimal chance;

    @Column(name = "drop_order")
    private Integer dropOrder;
}
