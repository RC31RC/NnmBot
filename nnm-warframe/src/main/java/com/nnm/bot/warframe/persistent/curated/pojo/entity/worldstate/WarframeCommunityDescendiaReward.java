package com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRow;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 经人工核对来源的楼层奖池映射；官方奖池内容留在 DROP_TABLE。 */
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
    @Column(name = "official_pool_key", length = 500)
    private String officialPoolKey;
    @Column(name = "expected_pool_name", columnDefinition = "TEXT")
    private String expectedPoolName;
    @Column(name = "source_reward_key", length = 500)
    private String sourceRewardKey;
    @Column(name = "item_count_override")
    private Integer itemCountOverride;
    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "guaranteed", nullable = false)
    private boolean guaranteed;
    @Column(name = "source_url", nullable = false, columnDefinition = "TEXT")
    private String sourceUrl;
}
