package com.nnm.bot.warframe.runtime.pojo.query.steelincursion;

import java.time.Instant;

/** 钢铁侵袭快照的时间、关键词与分页条件。 */
public record WarframeSteelIncursionQuery(Instant from, Instant until, String keyword, Integer current, Integer size) {
}
