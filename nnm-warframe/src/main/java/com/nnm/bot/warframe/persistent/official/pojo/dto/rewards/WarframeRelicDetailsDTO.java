package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

import java.math.BigDecimal;
import java.util.List;

/** 已选完整根树内某精炼状态的展示资料，保留奖励和地点的源顺序。 */
public record WarframeRelicDetailsDTO(RelicDTO relic, List<RewardDTO> rewards, List<DropDTO> drops) {
    /**
     * 构造遗物详情传输对象，并保存初始化参数。
     *
     * @param relic 遗物。
     * @param rewards 奖励集合。
     * @param drops 掉落集合。
     */
    public WarframeRelicDetailsDTO {
        rewards = List.copyOf(rewards);
        drops = List.copyOf(drops);
    }

    public record RelicDTO(String recordKey, String relicName, Boolean vaulted, String quality,
                        String icon, String iconUrl, String sourceCode) {
    }

    public record RewardDTO(String rewardName, String rarity, BigDecimal chance) {
    }

    public record DropDTO(String locationText, String rarity, BigDecimal chance) {
    }
}
