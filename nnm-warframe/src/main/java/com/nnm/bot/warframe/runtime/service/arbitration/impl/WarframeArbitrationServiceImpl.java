package com.nnm.bot.warframe.runtime.service.arbitration.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore;
import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore.Snapshot;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationProperties;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** 仲裁视图只读取 JVM 内存快照，并按每次查询的当前时刻重新筛选。 */
@Service
public class WarframeArbitrationServiceImpl implements WarframeArbitrationService {
    /** 每个 JVM 独立持有最近一次成功刷新的有界内存快照。 */
    @Resource
    private WarframeArbitrationSnapshotStore snapshots;
    /** 日期、显示上限及高效任务筛选规则。 */
    @Resource
    private WarframeArbitrationProperties properties;
    /** 可测试的当前时刻，避免缓存冻结“当前任务”。 */
    @Resource
    private Clock warframeClock;

    /**
     * 按真实查询范围筛选最近一次成功的 JVM 内存快照，不在机器人请求中下载来源。
     *
     * @param query 当前业务的查询条件。
     * @return 仲裁排期对象。
     */
    @Override
    public ArbitrationScheduleDTO query(QueryType query) {
        Instant now = warframeClock.instant();
        LocalDate today = now.atZone(properties.getZone()).toLocalDate();
        Instant from = query == QueryType.TODAY
                ? today.atStartOfDay(properties.getZone()).toInstant() : now;
        Instant until = switch (query) {
            case CURRENT -> now.plusNanos(1);
            case TODAY -> today.plusDays(1).atStartOfDay(properties.getZone()).toInstant();
            default -> now.plus(properties.getPreviewDays(), ChronoUnit.DAYS);
        };
        Snapshot snapshot = snapshots.current();
        Instant fetchedAt = snapshot.fetchedAt();
        List<ArbitrationScheduleEntryDTO> all = snapshot.entries().stream()
                .filter(entry -> entry.expiry().isAfter(from) && entry.activation().isBefore(until)).toList();
        List<ArbitrationScheduleEntryDTO> entries = all.stream()
                .filter(record -> query != QueryType.CURRENT || !record.activation().isAfter(now))
                .filter(record -> query != QueryType.EFFICIENT
                        || (record.activation().isAfter(now) && efficient(record)))
                // 自然日查询展示全部已缓存时段，不受上游解析容量或未来预览上限截断。
                .limit(query == QueryType.TODAY ? Long.MAX_VALUE
                        : query == QueryType.CURRENT ? 1 : properties.getPreviewLimit())
                .toList();
        String notice = "第三方预测排期；以游戏内实际任务为准。";
        if (fetchedAt != null
                && !fetchedAt.plus(properties.getRefreshInterval()).isAfter(now)) {
            notice += " 数据已过刷新周期，当前展示最近一次成功获取的本地快照。";
        }
        if (query == QueryType.TODAY) {
            notice += " 日期按 " + properties.getZone() + "；缺失时段不补造。";
        }
        if (query == QueryType.EFFICIENT) {
            notice += " 筛选：" + String.join("/", properties.getEfficientPlanets())
                    + "；" + String.join("/", properties.getEfficientMissionTypes())
                    + "；" + String.join("/", properties.getEfficientEnemies()) + "。";
        }
        if (CollUtil.isEmpty(entries)) notice += " 所查范围没有已收录的匹配任务，不能据此判断游戏内没有任务。";
        if (query != QueryType.CURRENT && !covers(all, from, until)) {
            notice += " 所查时间范围排期不完整。";
        }
        return new ArbitrationScheduleDTO(entries, notice, fetchedAt, properties.getZone());
    }

    /**
     * 连续覆盖检查不补造缺失小时；用于提示预测数据的范围缺口。
     *
     * @param records 待批量处理的业务记录。
     * @param from 转换。
     * @param until 截止时间。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean covers(List<ArbitrationScheduleEntryDTO> records, Instant from, Instant until) {
        Instant end = from;
        for (ArbitrationScheduleEntryDTO record : records) {
            if (record.activation().isAfter(end)) return false;
            if (record.expiry().isAfter(end)) end = record.expiry();
        }
        return !end.isBefore(until);
    }

    /**
     * 高效任务必须同时满足类型、星球和阵营规则，不以精华表生成排期。
     *
     * @param record 当前业务记录。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean efficient(ArbitrationScheduleEntryDTO record) {
        return matches(properties.getEfficientMissionTypes(), record.missionType())
                && matches(properties.getEfficientPlanets(), record.planet())
                && matches(properties.getEfficientEnemies(), record.enemy());
    }

    /**
     * 中英文候选按完整名称忽略大小写匹配，不使用可能误命中的子串。
     *
     * @param values 待处理的值集合。
     * @param value 待校验或规范化的文本。
     * @return 满足数据是否符合指定匹配条件时为真，否则为假。
     */
    private boolean matches(List<String> values, String value) {
        return value != null && values.stream().anyMatch(candidate -> candidate.equalsIgnoreCase(value.trim()));
    }

}
