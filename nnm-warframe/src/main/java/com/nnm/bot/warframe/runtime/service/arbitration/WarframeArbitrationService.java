package com.nnm.bot.warframe.runtime.service.arbitration;

import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.runtime.pojo.query.arbitration.WarframeArbitrationQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.arbitration.WarframeArbitrationManagementVO;

/** 机器人仲裁视图的查询边界；只读取本实例已经采集的排期，不触发来源下载或入库。 */
public interface WarframeArbitrationService {
    /**
     * 按当前时刻、配置时区和视图规则查询，并返回预测、陈旧和覆盖缺口提示。
     *
     * @param query 当前业务的查询条件。
     * @return 仲裁排期对象。
     */
    ArbitrationScheduleDTO query(QueryType query);

    /**
     * 分页查询已解析排期，供管理端检索完整快照。
     *
     * @param query 时间范围、关键词和分页条件。
     * @return 快照状态和当前页排期。
     */
    WarframeArbitrationManagementVO page(WarframeArbitrationQuery query);
}
