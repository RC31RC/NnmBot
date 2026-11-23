package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

import java.math.BigDecimal;

/** 已采纳的静态赏金奖励条目，保留物品身份和图标供下游补全。 */
public record WarframeBountyPoolRewardDTO(String itemKey, String displayName, BigDecimal count, BigDecimal probability,
    String imageUrl) {
}
