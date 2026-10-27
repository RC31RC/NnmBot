package com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 唯一生效快照；数据库行锁覆盖整个采纳事务，而非仅覆盖方法体。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_dataset_head")
public class WarframeOfficialImportHead {
    /** 固定为 1 的单例主键，由初始化 SQL 创建。 */
    @Id
    private Integer id;
    /** 已采纳批次 UUID；尚未导入时为空，逻辑关联 batch.id。 */
    @Column(name = "batch_id", length = 36)
    private String batchId;
}
