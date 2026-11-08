package com.nnm.bot.warframe.runtime.pojo.query.riven;

/** 已切分后的紫卡周成交查询；error 为机器人语法错误，不传给业务 Service。 */
public record WarframeRivenWeeklyQuery(String item, Boolean rerolled, int page, String error) {
}
