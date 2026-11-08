package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** 一次审批中一个 JSON 文件的独占执行和分类提交进度。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_apply_file")
public class WarframeOfficialApplyFile {
    @Id
    private UUID id;
    @Column(name = "batch_id", nullable = false, length = 36)
    private String batchId;
    @Column(name = "product_path", nullable = false, length = 260)
    private String productPath;
    @Column(nullable = false, length = 16)
    private String status;
    @Column(name = "added_done", nullable = false)
    private boolean addedDone;
    @Column(name = "changed_done", nullable = false)
    private boolean changedDone;
    @Column(name = "removed_done", nullable = false)
    private boolean removedDone;
    @Column(name = "owner_token")
    private UUID ownerToken;
    @Column(name = "lease_until")
    private Instant leaseUntil;
    @Column(nullable = false)
    private int attempts;
    @Column(columnDefinition = "TEXT")
    private String error;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
