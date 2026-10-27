package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** 用 JSON Pointer 定位变化字段；没有稳定成员键的数组按整体值比较。 */
public record WarframeOfficialFieldChangeDTO(
        String pointer,
        String previousJson,
        String incomingJson) {
}
