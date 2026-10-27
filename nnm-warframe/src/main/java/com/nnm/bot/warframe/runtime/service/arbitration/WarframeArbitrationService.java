package com.nnm.bot.warframe.runtime.service.arbitration;

import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;

/** 机器人仲裁视图的查询边界；只读取管理员已采纳排期，不触发来源下载或入库。 */
public interface WarframeArbitrationService {
    /**
     * 按当前时刻、配置时区和视图规则查询，并返回预测、陈旧和覆盖缺口提示。
     *
     * @param query 当前业务的查询条件。
     * @return 仲裁排期对象。
     */
    ArbitrationScheduleDTO query(QueryType query);
}
