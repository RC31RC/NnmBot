package com.nnm.bot.warframe.runtime.pojo.query.riven;

/** 紫卡周成交快照的业务查询，与命令解析 DTO 独立。 */
public record WarframeRivenWeeklyQuery(String platform, String keyword, Boolean rerolled, Integer current,
    Integer size) {
}
