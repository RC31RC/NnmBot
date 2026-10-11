package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wf_community_relic")
/**
 * 社区遗物主数据。
 *
 * <p>遗物奖励和掉落位置使用独立数据集保存，避免把不同职责混在一条记录中。</p>
 */
public class WarframeCommunityRelic extends WarframeCommunityDatasetRecord {
    @Column(name = "relic_name", nullable = false, length = 200)
    private String relicName;
    @Column(name = "unique_name", length = 500)
    private String uniqueName;
    @Column(length = 80)
    private String category;
    @Column(length = 80)
    private String era;
    @Column(length = 500)
    private String icon;
    @Column(name = "codex_secret")
    private Boolean codexSecret;
    @Column(name = "description_key", length = 500)
    private String descriptionKey;
    @Column(length = 40)
    private String quality;
    @Column(name = "reward_manifest", length = 500)
    private String rewardManifest;
    @Column(name = "introduced_at")
    private Long introducedAt;
    @Column(name = "market_id", length = 100)
    private String marketId;
    @Column(name = "url_name", length = 200)
    private String urlName;
    private Boolean vaulted;
}
