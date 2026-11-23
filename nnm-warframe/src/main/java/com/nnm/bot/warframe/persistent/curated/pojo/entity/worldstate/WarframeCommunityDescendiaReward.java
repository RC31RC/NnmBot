package com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRow;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 人工核对的楼层、难度与固定奖励或候选池映射。通过 DROP_TABLE 查询候选内容和概率。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_descendia_reward")
public class WarframeCommunityDescendiaReward extends WarframeCommunityRow {
    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber;
    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;
    @Column(name = "label", nullable = false, columnDefinition = "TEXT")
    private String label;
    /** 已采纳掉落表的来源业务键，用于联查池内容。 */
    @Column(name = "official_pool_key", nullable = false, length = 500)
    private String officialPoolKey;
    /** 核对来源池名称，避免采用重排后同键的其他奖池。 */
    @Column(name = "expected_pool_name", nullable = false, columnDefinition = "TEXT")
    private String expectedPoolName;
    @Column(name = "item_count_override")
    private Integer itemCountOverride;
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "guaranteed", nullable = false)
    private boolean guaranteed;
    @Column(name = "source_url", nullable = false, columnDefinition = "TEXT")
    private String sourceUrl;
}
