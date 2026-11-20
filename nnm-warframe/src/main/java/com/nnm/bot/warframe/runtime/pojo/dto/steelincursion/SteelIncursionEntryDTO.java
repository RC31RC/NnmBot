package com.nnm.bot.warframe.runtime.pojo.dto.steelincursion;

import java.time.Instant;

/** browse.wf 每日钢铁侵袭中的一个星图任务节点。 */
public record SteelIncursionEntryDTO(String id, Instant activation, Instant expiry, String node, String nodeReference,
    String planet, String missionType, String enemy) {
}
