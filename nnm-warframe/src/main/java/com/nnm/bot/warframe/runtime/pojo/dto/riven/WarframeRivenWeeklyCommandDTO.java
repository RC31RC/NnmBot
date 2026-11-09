package com.nnm.bot.warframe.runtime.pojo.dto.riven;

/** 已切分后的紫卡周成交查询；error 为机器人语法错误，不传给业务 Service。 */
public record WarframeRivenWeeklyCommandDTO(String item, Boolean rerolled, int page, String error) {
}
