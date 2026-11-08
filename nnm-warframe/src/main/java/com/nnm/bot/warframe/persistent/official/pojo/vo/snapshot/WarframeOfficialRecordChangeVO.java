package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

import java.util.List;

/** 已按新增、修改、删除分类查询的记录差异；分类不重复进入记录字段。 */
public record WarframeOfficialRecordChangeVO(String path, String recordKey, String displayName,
    List<WarframeOfficialFieldChangeVO> fieldChanges) {
}
