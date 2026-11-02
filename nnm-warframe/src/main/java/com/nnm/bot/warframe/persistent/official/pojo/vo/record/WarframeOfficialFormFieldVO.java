package com.nnm.bot.warframe.persistent.official.pojo.vo.record;

import java.util.List;

/** 管理端自维护表单字段结构。 */
public record WarframeOfficialFormFieldVO(String key, String label, String kind,
    List<WarframeOfficialFormFieldVO> fields, WarframeOfficialFormFieldVO item) {
}
