package com.nnm.bot.warframe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport 战甲结构化字段。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_warframe")
public class WarframeOfficialWarframe {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    private Integer health;
    private Integer shield;
    private Integer armor;
    private Integer stamina;
    private Integer power;
    @Column(name = "mastery_req")
    private Integer masteryReq;
    @Column(name = "sprint_speed")
    private Double sprintSpeed;
    @Column(name = "codex_secret")
    private Boolean codexSecret;
    @Column(name = "abilities_json", columnDefinition = "TEXT")
    private String abilitiesJson;
    @Column(name = "exalted_json", columnDefinition = "TEXT")
    private String exaltedJson;
    @Column(name = "stats_json", columnDefinition = "TEXT")
    private String statsJson;
    @Column(name = "tags_json", columnDefinition = "TEXT")
    private String tagsJson;
}
