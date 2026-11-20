package com.nnm.bot.warframe.runtime.pojo.query.kuva;

/** 赤毒任务快照的管理端分页查询。 */
public record WarframeKuvaMissionQuery(String keyword, Integer current, Integer size) {
}
