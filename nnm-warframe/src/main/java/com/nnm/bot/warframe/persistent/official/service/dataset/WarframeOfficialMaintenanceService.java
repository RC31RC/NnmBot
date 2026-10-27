package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;

public interface WarframeOfficialMaintenanceService {

    /**
     * 显式保存当前业务记录。
     *
     * @param id 目标记录的本地主键。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 游戏资料官方记录对象。
     */
    WarframeOfficialRecordDTO save(Long id, WarframeOfficialRecordSaveDTO request);

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param recordId 当前记录的本地主键。
     */
    void delete(long recordId);

    /**
     * 将选定官方记录及其详情转换为同模型的自维护数据。
     *
     * @param recordId 当前记录的本地主键。
     * @param confirmed 调用方是否已确认当前变更。
     * @param replaceExisting 是否允许替换同身份已有数据。
     * @return 游戏资料官方记录对象。
     */
    WarframeOfficialRecordDTO convertToSelfMaintained(long recordId, boolean confirmed, boolean replaceExisting);
}
