package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_fish_guide")
/**
 * 参考项目中的鱼类攻略数据。
 *
 * <p>除结构化字段外，完整上游内容保存在统一记录基类的 JSON 字段中，
 * 便于后续按原始格式重新导入和人工合并。</p>
 */
public class WarframeCommunityFishGuide extends WarframeCommunityDatasetRecord {
    @Column(length = 200)
    private String name;
}
