package com.nnm.bot.warframe.persistent.official.pojo.dto.support;

/** 非受管的官方 JSON 输入；原文另行保留，以区分缺失字段与显式 null。 */
public interface OfficialRecordDTO {
    /**
     * 返回本记录供检索使用的公共摘要。
     *
     * @return 用于检索的名称、分类与说明摘要。
     */
    OfficialRecordHeaderDTO header();

    /**
     * 返回上游明确提供的稳定业务键。
     *
     * @return 上游提供的稳定业务键；缺省时为空。
     */
    String stableKey();
}
