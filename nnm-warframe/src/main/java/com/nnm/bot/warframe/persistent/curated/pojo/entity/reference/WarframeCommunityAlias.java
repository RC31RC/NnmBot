package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 玩家搜索物品时使用的社区简称和正式名称。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_alias")
public class WarframeCommunityAlias extends WarframeCommunityRow {
    /** 玩家输入的社区简称。 */
    @Column(name = "alias_name", nullable = false, columnDefinition = "TEXT")
    private String aliasName;

    /** 供下一步查库确认的目标名称或完整业务键。 */
    @Column(name = "target_name", nullable = false, columnDefinition = "TEXT")
    private String targetName;

}
