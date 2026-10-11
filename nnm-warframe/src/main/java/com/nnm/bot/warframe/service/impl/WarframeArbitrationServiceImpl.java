package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.application.WarframeArbitrationService;
import com.nnm.bot.warframe.config.properties.WarframeArbitrationProperties;
import com.nnm.bot.warframe.model.ArbitrationQuery;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;
import com.nnm.bot.warframe.model.ArbitrationScheduleResult;
import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import com.nnm.bot.warframe.repository.WarframeArbitrationScheduleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class WarframeArbitrationServiceImpl implements WarframeArbitrationService {
    @Resource
    private WarframeArbitrationScheduleRepository repository;
    @Resource
    private WarframeArbitrationProperties properties;
    @Resource
    private Clock warframeClock;

    public ArbitrationScheduleResult query(ArbitrationQuery query) {
        Instant now = warframeClock.instant();
        var today = now.atZone(properties.getZone()).toLocalDate();
        Instant from = query == ArbitrationQuery.TODAY
                ? today.atStartOfDay(properties.getZone()).toInstant() : now;
        Instant until = switch (query) {
            case CURRENT -> now.plusNanos(1);
            case TODAY -> today.plusDays(1).atStartOfDay(properties.getZone()).toInstant();
            default -> now.plus(properties.getPreviewDays(), ChronoUnit.DAYS);
        };
        var records = repository.findByExpiryGreaterThanAndActivationLessThanOrderByActivationAsc(from, until);
        List<ArbitrationScheduleEntry> entries = records.stream()
                .filter(record -> query != ArbitrationQuery.CURRENT || !record.getActivation().isAfter(now))
                .filter(record -> query != ArbitrationQuery.EFFICIENT
                        || (record.getActivation().isAfter(now) && efficient(record)))
                .limit(query == ArbitrationQuery.TODAY ? properties.getMaxRecords()
                        : query == ArbitrationQuery.CURRENT ? 1 : properties.getPreviewLimit())
                .map(this::entry).toList();
        Instant fetchedAt = records.stream().map(WarframeArbitrationSchedule::getFetchedAt)
                .min(Instant::compareTo).orElse(null);
        String notice = "第三方预测排期；以游戏内实际任务为准。";
        if (fetchedAt != null
                && !fetchedAt.plus(properties.getRefreshInterval()).isAfter(now)) {
            notice += " 数据已过刷新周期，当前展示本地存档。";
        }
        if (query == ArbitrationQuery.TODAY) {
            notice += " 日期按 " + properties.getZone() + "；过去时段只展示已存档记录。";
        }
        if (query == ArbitrationQuery.EFFICIENT) {
            notice += " 筛选：" + String.join("/", properties.getEfficientPlanets())
                    + "；" + String.join("/", properties.getEfficientMissionTypes())
                    + "；" + String.join("/", properties.getEfficientEnemies()) + "。";
        }
        if (entries.isEmpty()) notice += " 所查范围没有已收录的匹配任务，不能据此判断游戏内没有任务。";
        if (query != ArbitrationQuery.CURRENT && !covers(records, from, until)) {
            notice += " 所查时间范围排期不完整。";
        }
        return new ArbitrationScheduleResult(entries, notice, fetchedAt, properties.getZone());
    }

    private boolean covers(List<WarframeArbitrationSchedule> records, Instant from, Instant until) {
        Instant end = from;
        for (var record : records) {
            if (record.getActivation().isAfter(end)) return false;
            if (record.getExpiry().isAfter(end)) end = record.getExpiry();
        }
        return !end.isBefore(until);
    }

    private boolean efficient(WarframeArbitrationSchedule record) {
        return matches(properties.getEfficientMissionTypes(), record.getMissionType())
                && matches(properties.getEfficientPlanets(), record.getPlanet())
                && matches(properties.getEfficientEnemies(), record.getEnemy());
    }

    private boolean matches(List<String> values, String value) {
        return value != null && values.stream().anyMatch(candidate -> candidate.equalsIgnoreCase(value.trim()));
    }

    private ArbitrationScheduleEntry entry(WarframeArbitrationSchedule record) {
        return new ArbitrationScheduleEntry(record.getUpstreamId(), record.getActivation(), record.getExpiry(),
                record.getNode(), record.getMissionType(), record.getEnemy(), record.getPlanet(), record.getBoundsJson());
    }
}
