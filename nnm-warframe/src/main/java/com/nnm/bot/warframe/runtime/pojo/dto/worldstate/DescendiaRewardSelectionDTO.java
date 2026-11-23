package com.nnm.bot.warframe.runtime.pojo.dto.worldstate;

/** 按周标识保存指定楼层和难度的人工奖励确认；选项为空时清除。 */
public record DescendiaRewardSelectionDTO(String week, Integer floor, String difficulty, String optionKey) {
}
