package com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRow;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** 人工审核的八周轮换顺序；商品名称、图片与价格只读取已入库的官方商人数据。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_steel_path_rotation")
public class WarframeCommunitySteelPathRotation extends WarframeCommunityRow {
    @Column(name = "rotation_index", nullable = false)
    private Integer rotationIndex;
    @Column(name = "anchor_week", nullable = false)
    private LocalDate anchorWeek;
    @Column(name = "store_item", nullable = false, length = 500)
    private String storeItem;
}
