package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport MOD 和升级项结构化字段。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade")
public class WarframeOfficialUpgrade {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    @Column(name = "base_drain")
    private Integer baseDrain;
    @Column(name = "fusion_limit")
    private Integer fusionLimit;
    @Column(name = "compat_name", length = 300)
    private String compatName;
    @Column(length = 160)
    private String type;
    @Column(length = 160)
    private String subtype;
    @Column(length = 120)
    private String polarity;
    @Column(length = 120)
    private String rarity;
    @Column(name = "is_utility")
    private Boolean utility;
    @Column(name = "is_augment")
    private Boolean augment;
    @Column(name = "is_exilus")
    private Boolean exilus;
    @Column(name = "num_upgrades_in_set")
    private Integer numUpgradesInSet;
    @Column(name = "mod_set", length = 300)
    private String modSet;
    @Column(name = "mod_set_values_json", columnDefinition = "TEXT")
    private String modSetValuesJson;
    @Column(name = "available_challenges_json", columnDefinition = "TEXT")
    private String availableChallengesJson;
    @Column(name = "level_stats_json", columnDefinition = "TEXT")
    private String levelStatsJson;
    @Column(name = "upgrade_entries_json", columnDefinition = "TEXT")
    private String upgradeEntriesJson;
    @Column(name = "tags_json", columnDefinition = "TEXT")
    private String tagsJson;
}
