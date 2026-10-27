package com.nnm.bot.warframe.persistent.official.pojo.entity.maintenance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** 记录本地删除或转为自维护的官方主记录，独立于文件摘要跟踪重建需求。 */
@Getter
@Setter
@Entity
@Table(
        name = "wf_official_rebuild_marker",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_wf_official_rebuild_marker_identity",
                columnNames = {"product_path", "domain_code", "bkey"}))
public class WarframeOfficialRebuildMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_path", nullable = false, length = 260)
    private String productPath;

    @Column(name = "domain_code", nullable = false, length = 120)
    private String domainCode;

    @Column(name = "bkey", nullable = false, length = 500)
    private String bkey;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;
}
