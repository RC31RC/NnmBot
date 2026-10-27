package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

/** 字段差异的展示对象；没有稳定成员键的数组按整体值比较。 */
public record WarframeOfficialFieldChangeVO(
        String pointer,
        String previousJson,
        String incomingJson) {
}
