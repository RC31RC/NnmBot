package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

/** 每个官方 JSON 文件的采纳与候选版本状态；仅待审候选原文暂存在对象存储。 */
@Getter
@Setter
// 候选元数据可能与业务写入并发更新，不能把读取时的旧数据版本写回数据库。
@DynamicUpdate
@Entity
@Table(name = "wf_official_dataset_file")
public class WarframeOfficialFile {

    @Id
    @Column(name = "product_path", nullable = false, length = 260)
    private String productPath;

    /** 数据库可见内容版本；采纳和人工维护均在原事务内递增。 */
    @Column(name = "data_revision", nullable = false)
    private long dataRevision;

    @Column(name = "adopted_sha256", length = 64)
    private String adoptedSha256;

    @Column(name = "candidate_sha256", length = 64)
    private String candidateSha256;

    @Column(name = "json_changed", nullable = false)
    private boolean jsonChanged;

    @Column(name = "adopted_record_count")
    private Long adoptedRecordCount;

    @Column(name = "candidate_record_count")
    private Long candidateRecordCount;

    @Column(name = "source_ref", length = 200)
    private String sourceRef;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "commit_sha", length = 40)
    private String commitSha;

    @Column(name = "build_id", length = 160)
    private String buildId;

    @Column(name = "candidate_source_ref", length = 200)
    private String candidateSourceRef;

    @Column(name = "candidate_source_url", columnDefinition = "TEXT")
    private String candidateSourceUrl;

    @Column(name = "candidate_commit_sha", length = 40)
    private String candidateCommitSha;

    @Column(name = "candidate_build_id", length = 160)
    private String candidateBuildId;

    @Column(name = "candidate_parser_contract_version")
    private Integer candidateParserContractVersion;

    @Column(name = "parser_contract_version", nullable = false)
    private int parserContractVersion = 1;

    @Column(name = "adopted_at")
    private Instant adoptedAt;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;
}
