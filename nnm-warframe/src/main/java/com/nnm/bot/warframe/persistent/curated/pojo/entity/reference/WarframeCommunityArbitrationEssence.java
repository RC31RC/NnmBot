package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import com.nnm.bot.warframe.persistent.curated.constant.WarframeArbitrationEfficiencyGrade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 按星图节点键维护的仲裁收益资料，用于展示预期效率并筛选高效节点。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_arbitration_essence")
public class WarframeCommunityArbitrationEssence extends WarframeCommunityRow {
    @Column(name = "node_key", columnDefinition = "TEXT")
    private String nodeKey;

    @Column(name = "node_name", nullable = false, columnDefinition = "TEXT")
    private String nodeName;

    @Column(name = "system_name", columnDefinition = "TEXT")
    private String systemName;

    /** 每小时仲裁精华估值；空值表示该节点无已采纳的样本或估值。 */
    @Column(name = "essence_amount")
    private Integer essenceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "efficiency_grade", length = 1, columnDefinition = "varchar(1)")
    private WarframeArbitrationEfficiencyGrade efficiencyGrade;

}
