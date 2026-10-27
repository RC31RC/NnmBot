package com.nnm.bot.warframe.runtime.pojo.dto.arbitration;

import java.time.Instant;

/**
 * 仲裁运行时解析与内存查询共用的不可变排期行。
 *
 * @param id 上游排期身份；不是数据库自增 ID
 * @param activation 开始时刻，含此边界，也是排期快照中的小时身份
 * @param expiry 结束时刻，不含此边界
 * @param node 经代码簿解析的节点显示名
 * @param missionType 经代码簿解析的任务类型，用于高效筛选
 * @param enemy 经代码簿解析的敌方阵营
 * @param planet 经代码簿解析的星球，用于高效筛选
 * @param boundsJson 上游边界信息；arbys.txt 未提供时保留空对象，不推算补造
 */
public record ArbitrationScheduleEntryDTO(
        String id, Instant activation, Instant expiry, String node, String missionType,
        String enemy, String planet, String boundsJson) {}
