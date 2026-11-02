package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** 单个官方 JSON 文件的审核批次；审批前不参与任何业务查询。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_dataset_batch")
public class WarframeOfficialImportBatch {
    /** 本轮预览 UUID；也是审批幂等键。 */
    @Id
    @Column(length = 36)
    private String id;
    /** 管理员选定的官方数据 tag，不是可变分支。 */
    @Column(name = "source_ref", nullable = false, length = 200)
    private String sourceRef;
    /** 该 tag 解析到的完整 Git 提交，仅作为版本审计值。 */
    @Column(name = "commit_sha", nullable = false, length = 40)
    private String commitSha;
    /** 发布器生成的构建标识，不解析为业务主键。 */
    @Column(name = "build_id", nullable = false, length = 160)
    private String buildId;
    /** 完整成品清单；不保存内部采集出处。 */
    @Column(name = "manifest_json", nullable = false, columnDefinition = "TEXT")
    private String manifestJson;
    /** 本次审核对应的官方 JSON 静态路径。 */
    @Column(name = "product_path", length = 260)
    private String productPath;
    /** 已冻结文件及记录差异的摘要，不随业务库后续变化重新计算。 */
    @Column(name = "preview_json", nullable = false, columnDefinition = "TEXT")
    private String previewJson;
    /** 对象存储中冻结的字段级记录差异文件键。 */
    @Column(name = "change_object_key", nullable = false, length = 300)
    private String changeObjectKey;
    /** 生成预览时的生效批次；首次导入为空，用于阻止过期审批覆盖新版本。 */
    @Column(name = "baseline_id", length = 36)
    private String baselineId;
    /** 预览生成时已支持领域的数据库指纹；审批时用于校验人工变动和并发刷新。 */
    @Column(name = "baseline_hash", nullable = false, length = 64)
    private String baselineHash;
    /** 固定 adapter 契约版本，升级后必须重新生成预览。 */
    @Column(name = "parser_contract_version", nullable = false)
    private int parserContractVersion;
    /** PENDING 待审核，APPLIED 已采纳且保留，EXPIRED 预览过期，ARCHIVED 历史元数据归档。 */
    @Column(nullable = false, length = 16)
    private String status;
    /** 本轮单文件校验结束时间，单位为绝对时间。 */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    /** 待审核有效期；已经采纳的快照不按此字段清理。 */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
