package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** 一轮官方 JSON 审核批次；待审候选不参与业务查询，采纳失败的批次和文件错误在原件过期后仍保留审计。 */
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
    /** 一次审批的冻结选择；再次提交不得覆盖。 */
    @Column(name = "apply_selection_json", columnDefinition = "TEXT")
    private String applySelectionJson;
    /** 经过对象存储校验后的剩余差异位置及统计。 */
    @Column(name = "apply_plan_json", columnDefinition = "TEXT")
    private String applyPlanJson;
    /** 准备失败或部分文件失败的持久化摘要。 */
    @Column(name = "apply_error", columnDefinition = "TEXT")
    private String applyError;
    /** 首次采纳失败后的原件保留截止时间；人工重试不延长，运行中的文件不被回收。 */
    @Column(name = "retry_expires_at")
    private Instant retryExpiresAt;
    /** 对象存储回收完成的时间；保留过期失败的数据库错误与文件进度。 */
    @Column(name = "objects_released_at")
    private Instant objectsReleasedAt;
    /** 生成预览时的生效批次；首次导入为空，用于阻止过期审批覆盖新版本。 */
    @Column(name = "baseline_id", length = 36)
    private String baselineId;
    /** 固定 adapter 契约版本，升级后必须重新生成预览。 */
    @Column(name = "parser_contract_version", nullable = false)
    private int parserContractVersion;
    /** PENDING 待审核，APPLYING 正在采纳，APPLIED 已采纳且保留，EXPIRED 预览过期，ARCHIVED 回收中的临时状态。 */
    @Column(nullable = false, length = 16)
    private String status;
    /** 最近一次采纳心跳；与文件锁及数据库行锁结合判断异常遗留的操作。 */
    @Column(name = "applying_at")
    private Instant applyingAt;
    /** 本轮单文件校验结束时间，单位为绝对时间。 */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    /** 待审核有效期；已采纳批次只通过当前指针保留元数据。 */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
