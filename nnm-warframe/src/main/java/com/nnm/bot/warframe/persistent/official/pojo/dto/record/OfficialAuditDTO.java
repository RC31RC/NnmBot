package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.OfficialSummaryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;

/** 管理详情投影；原始输入只用于审核，不参与命令详情重建。 */
public interface OfficialAuditDTO extends OfficialSummaryDTO {
    /**
     * 读取用于审核的原始 JSON 文本。
     *
     * @return 用于审核的原始 JSON 文本。
     */
    String getRawJson();

    /**
     * 读取用于审核的本地化原始 JSON 文本。
     *
     * @return 用于审核的本地化原始 JSON 文本。
     */
    String getLocalizedRawJson();

    /**
     * 将当前只读投影转换为非受管的数据对象。
     *
     * @return 游戏资料官方记录对象。
     */
    @Override
    default WarframeOfficialRecordDTO toData() {
        WarframeOfficialRecordDTO data = OfficialSummaryDTO.super.toData();
        data.setRawJson(getRawJson());
        data.setLocalizedRawJson(getLocalizedRawJson());
        return data;
    }
}
