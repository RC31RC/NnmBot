package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_map_feature")
/**
 * 参考项目中的地图标注。
 *
 * <p>currentJson 保留完整 GeoJSON，结构化字段只用于列表展示和筛选。</p>
 */
public class WarframeCommunityMapFeature extends WarframeCommunityDatasetRecord {
    @Column(nullable = false, length = 32)
    private String world;
    @Column(name = "feature_type", length = 80)
    private String featureType;
    @Column(length = 300)
    private String name;
    @Column(name = "geometry_type", length = 32)
    private String geometryType;
}
