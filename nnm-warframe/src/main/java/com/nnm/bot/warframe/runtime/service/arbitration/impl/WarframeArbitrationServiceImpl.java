package com.nnm.bot.warframe.runtime.service.arbitration.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.EssenceDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore;
import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore.Snapshot;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationProperties;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationSourceProperties;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.runtime.constant.WarframeArbitrationErrorCode;
import com.nnm.bot.warframe.runtime.parser.arbitration.WarframeArbitrationScheduleParser;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.query.arbitration.WarframeArbitrationQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.arbitration.WarframeArbitrationManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.arbitration.WarframeArbitrationManagementVO.EntryVO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nnm.bot.warframe.runtime.constant.WarframeRuntimeQueryConstants.DEFAULT_PAGE_SIZE;
import static com.nnm.bot.warframe.runtime.constant.WarframeRuntimeQueryConstants.MAX_PAGE_SIZE;

/** 仲裁视图只读取 JVM 内存快照，并按每次查询的当前时刻重新筛选。 */
@Service
@Slf4j
public class WarframeArbitrationServiceImpl implements WarframeArbitrationService {
    /** 每个 JVM 独立持有最近一次成功刷新的有界内存快照。 */
    @Resource
    private WarframeArbitrationSnapshotStore snapshots;
    @Resource
    private WarframeArbitrationScheduleParser parser;
    /** 日期、显示上限及高效任务筛选规则。 */
    @Resource
    private WarframeArbitrationProperties properties;
    /** 可测试的当前时刻，避免缓存冻结“当前任务”。 */
    @Resource
    private Clock warframeClock;
    @Resource
    private WarframeArbitrationSourceProperties sourceProperties;
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

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
        Instant from = query == QueryType.TODAY ? today.atStartOfDay(properties.getZone()).toInstant() : now;
        Instant until = switch (query) {
            case CURRENT -> now.plusNanos(1);
            case TODAY -> today.plusDays(1).atStartOfDay(properties.getZone()).toInstant();
            default -> now.plus(properties.getPreviewDays(), ChronoUnit.DAYS);
        };
        Snapshot snapshot = snapshots.current();
        Instant fetchedAt = snapshot.fetchedAt();
        List<ArbitrationScheduleEntryDTO> all = snapshot.entries().stream()
            .filter(entry -> entry.expiry().isAfter(from) && entry.activation().isBefore(until)).toList();
        if (all.stream().anyMatch(entry -> entry.nodeReference() != null)) {
            try {
                all = parser.reproject(all);
            } catch (RuntimeException exception) {
                // 静态资料不可用时保留已解释的有效排期，不触发上游重新下载。
                log.warn("仲裁静态引用重投影失败，使用有效快照", exception);
            }
        }
        List<ArbitrationScheduleEntryDTO> entries = all.stream()
            .filter(record -> query != QueryType.CURRENT || !record.activation().isAfter(now))
            .filter(record -> query != QueryType.EFFICIENT || (record.activation().isAfter(now) && efficient(record)))
            // 自然日查询展示全部已缓存时段，不受上游解析容量或未来预览上限截断。
            .limit(query == QueryType.TODAY ? Long.MAX_VALUE
                : query == QueryType.CURRENT ? 1 : properties.getPreviewLimit())
            .toList();
        String notice = "第三方预测排期；以游戏内实际任务为准。";
        if (fetchedAt != null && !fetchedAt.plus(properties.getStaleAfter()).isAfter(now)) {
            notice += " 数据已过刷新周期，当前展示最近一次成功获取的本地快照。";
        }
        if (query == QueryType.TODAY) {
            notice += " 日期按 " + properties.getZone() + "；缺失时段不补造。";
        }
        if (query == QueryType.EFFICIENT) {
            notice += " 筛选：" + String.join("/", properties.getEfficientPlanets()) + "；"
                + String.join("/", properties.getEfficientMissionTypes()) + "；"
                + String.join("/", properties.getEfficientEnemies()) + "。";
        }
        if (CollUtil.isEmpty(entries))
            notice += " 所查范围没有已收录的匹配任务，不能据此判断游戏内没有任务。";
        if (query != QueryType.CURRENT && !covers(all, from, until)) {
            notice += " 所查时间范围排期不完整。";
        }
        return new ArbitrationScheduleDTO(withCommunityReferences(entries), notice, fetchedAt, properties.getZone());
    }

    /**
     * 查询快照中的任意时间范围；只对当前页重解释静态引用。
     *
     * @param query 时间、关键词和分页条件。
     * @return 完整快照元数据及分页后的展示行。
     */
    @Override
    public WarframeArbitrationManagementVO page(WarframeArbitrationQuery query) {
        int current = ObjUtil.defaultIfNull(query.current(), 1);
        int size = ObjUtil.defaultIfNull(query.size(), DEFAULT_PAGE_SIZE);
        BusinessAssert.isTrue(current > 0 && size > 0 && size <= MAX_PAGE_SIZE,
            WarframeArbitrationErrorCode.QUERY_INVALID);
        BusinessAssert.isTrue(query.from() == null || query.until() == null || query.from().isBefore(query.until()),
            WarframeArbitrationErrorCode.TIME_RANGE_INVALID);
        String keyword = CharSequenceUtil.trimToEmpty(query.keyword());
        Snapshot snapshot = snapshots.current();
        List<ArbitrationScheduleEntryDTO> matches =
            snapshot.entries().stream().filter(entry -> query.from() == null || entry.expiry().isAfter(query.from()))
                .filter(entry -> query.until() == null || entry.activation().isBefore(query.until()))
                .filter(entry -> CharSequenceUtil.isEmpty(keyword) || Stream
                    .of(entry.node(), entry.missionType(), entry.enemy(), entry.planet(), entry.nodeReference())
                    .anyMatch(value -> CharSequenceUtil.containsIgnoreCase(value, keyword)))
                .toList();
        int start = (int)Math.min((current - 1L) * size, matches.size());
        List<ArbitrationScheduleEntryDTO> page = matches.subList(start, Math.min(start + size, matches.size()));
        if (page.stream().anyMatch(entry -> entry.nodeReference() != null)) {
            try {
                page = parser.reproject(page);
            } catch (RuntimeException exception) {
                log.warn("仲裁管理页静态引用重投影失败，保留有效快照", exception);
            }
        }
        page = withCommunityReferences(page);
        List<EntryVO> records = page.stream()
            .map(entry -> new EntryVO(entry.id(), entry.activation(), entry.expiry(), entry.node(), entry.missionType(),
                entry.enemy(), entry.planet(), entry.boundsJson(), entry.nodeReference(), entry.essenceAmount(),
                entry.efficiencyGrade()))
            .toList();
        Instant fetchedAt = snapshot.fetchedAt();
        boolean stale = fetchedAt != null
            && !warframeClock.instant().isBefore(fetchedAt.plus(sourceProperties.getRefreshInterval()));
        Instant from = CollUtil.isEmpty(snapshot.entries()) ? null : snapshot.entries().getFirst().activation();
        Instant until = CollUtil.isEmpty(snapshot.entries()) ? null : snapshot.entries().getLast().expiry();
        return new WarframeArbitrationManagementVO(fetchedAt != null, stale, sourceProperties.getUrl().toString(),
            fetchedAt, from, until, PageResult.of(records, current, size, matches.size()),
            "第三方预测排期，以游戏内实际任务为准；当前页面只查询本实例快照。");
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
            if (record.activation().isAfter(end))
                return false;
            if (record.expiry().isAfter(end))
                end = record.expiry();
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

    /** 仅把已入库的节点估值与评级附加到本次结果，不改变上游排期快照。 */
    private List<ArbitrationScheduleEntryDTO> withCommunityReferences(List<ArbitrationScheduleEntryDTO> entries) {
        List<WarframeCommunityRecordDTO<EssenceDTO>> records = communityCatalog.arbitrationEssences();
        if (records.isEmpty())
            return entries;
        Map<String, EssenceDTO> byNode = new LinkedHashMap<>();
        for (WarframeCommunityRecordDTO<EssenceDTO> record : records) {
            EssenceDTO details = record.details();
            String nodeKey = CharSequenceUtil.isNotBlank(details.nodeKey()) ? details.nodeKey() : record.recordKey();
            if (CharSequenceUtil.isNotBlank(nodeKey))
                byNode.putIfAbsent(nodeKey, details);
        }
        return entries.stream().map(entry -> {
            EssenceDTO details = entry.nodeReference() == null ? null : byNode.get(entry.nodeReference());
            if (details == null) {
                details = records.stream().map(WarframeCommunityRecordDTO::details)
                    .filter(value -> CharSequenceUtil.equalsIgnoreCase(value.nodeName(), entry.node())).findFirst()
                    .orElse(null);
            }
            if (details == null)
                return entry;
            String grade = details.efficiencyGrade() == null ? null : details.efficiencyGrade().code();
            return entry.withCommunityReference(details.essenceAmount(), grade);
        }).toList();
    }

    /**
     * 中英文候选按完整名称忽略大小写匹配，不使用可能误命中的子串。
     *
     * @param values 待处理的值集合。
     * @param value 待校验或规范化的文本。
     * @return 满足数据是否符合指定匹配条件时为真，否则为假。
     */
    private boolean matches(List<String> values, String value) {
        return CollUtil.isNotEmpty(values) && CharSequenceUtil.isNotBlank(value) && values.stream()
            .anyMatch(candidate -> CharSequenceUtil.equalsIgnoreCase(candidate, CharSequenceUtil.trim(value)));
    }

}
