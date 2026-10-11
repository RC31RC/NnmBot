package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "wf_arbitration_schedule")
public class WarframeArbitrationSchedule {
    @Id
    @Column(nullable = false)
    private Instant activation;
    @Column(nullable = false)
    private Instant expiry;
    @Column(name = "upstream_id", nullable = false, length = 100)
    private String upstreamId;
    @Column(nullable = false, length = 200)
    private String node;
    @Column(name = "mission_type", nullable = false, length = 100)
    private String missionType;
    @Column(nullable = false, length = 100)
    private String enemy;
    @Column(nullable = false, length = 100)
    private String planet;
    @Column(name = "bounds_json", nullable = false, columnDefinition = "text")
    private String boundsJson;
    @Column(name = "source_url", nullable = false, columnDefinition = "text")
    private String sourceUrl;
    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;
}
