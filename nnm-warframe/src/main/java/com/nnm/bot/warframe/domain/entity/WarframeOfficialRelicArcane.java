package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport 遗物和奥术结构化字段。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_arcane")
public class WarframeOfficialRelicArcane {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    @Column(length = 160)
    private String type;
    @Column(length = 120)
    private String rarity;
    @Column(name = "level_stats_json", columnDefinition = "TEXT")
    private String levelStatsJson;
    @Column(name = "relic_rewards_json", columnDefinition = "TEXT")
    private String relicRewardsJson;
}
