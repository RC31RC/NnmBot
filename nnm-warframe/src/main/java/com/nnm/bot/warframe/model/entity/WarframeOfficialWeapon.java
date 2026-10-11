package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport 武器结构化字段，完整原始 JSON 保存在官方基础记录表。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_weapon")
public class WarframeOfficialWeapon {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    @Column(name = "mastery_req")
    private Integer masteryReq;
    @Column(name = "total_damage")
    private Double totalDamage;
    @Column(name = "damage_per_shot_json", columnDefinition = "TEXT")
    private String damagePerShotJson;
    @Column(name = "critical_chance")
    private Double criticalChance;
    @Column(name = "critical_multiplier")
    private Double criticalMultiplier;
    @Column(name = "proc_chance")
    private Double procChance;
    @Column(name = "fire_rate")
    private Double fireRate;
    @Column(name = "accuracy")
    private Double accuracy;
    @Column(name = "magazine_size")
    private Integer magazineSize;
    @Column(name = "reload_time")
    private Double reloadTime;
    @Column(name = "multishot")
    private Double multishot;
    @Column(name = "slot")
    private Integer slot;
    @Column(name = "noise", length = 120)
    private String noise;
    @Column(name = "trigger", length = 120)
    private String trigger;
    @Column(name = "omega_attenuation")
    private Double omegaAttenuation;
    @Column(name = "range_json", columnDefinition = "TEXT")
    private String rangeJson;
    @Column(name = "slam_attack")
    private Double slamAttack;
    @Column(name = "slide_attack")
    private Double slideAttack;
    @Column(name = "wind_up")
    private Double windUp;
    @Column(name = "level_stats_json", columnDefinition = "TEXT")
    private String levelStatsJson;
    @Column(name = "ingredients_json", columnDefinition = "TEXT")
    private String ingredientsJson;
    @Column(name = "tags_json", columnDefinition = "TEXT")
    private String tagsJson;
}
