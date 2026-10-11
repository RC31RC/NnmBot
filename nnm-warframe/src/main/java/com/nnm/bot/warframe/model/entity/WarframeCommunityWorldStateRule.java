package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_worldstate_rule")
/**
 * WorldState 规则汇总记录。
 *
 * <p>运行时按 datasetCode 和 locale 读取，并从 currentJson 中合并规则分区。</p>
 */
public class WarframeCommunityWorldStateRule extends WarframeCommunityDatasetRecord {
    @Column(name = "dataset_code", nullable = false, length = 80)
    private String datasetCode;
    @Column(nullable = false, length = 8)
    private String locale;
}
