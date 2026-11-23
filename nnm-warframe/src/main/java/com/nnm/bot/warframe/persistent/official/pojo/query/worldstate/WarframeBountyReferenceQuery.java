package com.nnm.bot.warframe.persistent.official.pojo.query.worldstate;

import java.util.Map;
import java.util.Set;

/** 本轮已通过动态身份校验的赏金引用及按集团指定的静态候选池。 */
public record WarframeBountyReferenceQuery(Set<String> nodes, Set<String> challenges,
    Map<String, String> poolsBySyndicate) {
}
