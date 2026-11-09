package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.RECORD_BATCH_SIZE;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.OfficialIdentityDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.OfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.OfficialSummaryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/** 仅复用单表查询代码；每个具体接口绑定自己的 Entity 和独立业务表。 */
@NoRepositoryBean
public interface OfficialMainRepository<T extends OfficialRecord>
    extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    WarframeOfficialDataDomain domain();

    /**
     * 创建当前主表的待写入记录。
     *
     * @return 尚未持久化的当前主表实体。
     */
    T newRecord();

    /**
     * 按记录维护状态及稳定业务键读取待维护实体，业务键比较忽略大小写。
     *
     * @param maintenanceType 官方记录的维护状态，不表示来源类别。
     * @param bkey 当前官方 JSON 记录的稳定业务键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<T> findByMaintenanceTypeAndBkeyIgnoreCase(MaintenanceType maintenanceType, String bkey);

    /**
     * 读取指定维护状态的全部非受管投影。
     *
     * @param maintenanceType 官方记录的维护状态，不表示来源类别。
     * @return 记录主键与稳定业务键的只读投影组成的列表。
     */
    List<OfficialIdentityDTO> findAllProjectedByMaintenanceType(MaintenanceType maintenanceType);

    /**
     * 读取当前独立主表的摘要投影。
     *
     * @return 当前独立官方记录的非受管摘要组成的列表。
     */
    default List<WarframeOfficialRecordDTO> findSummaries() {
        return findBy(preferredEnabled(), query -> query.as(OfficialSummaryDTO.class).all()).stream().map(this::toData)
            .toList();
    }

    /** 按已筛选 ID 分批读取运行时摘要，不读取审核原文。 */
    default List<WarframeOfficialRecordDTO> findSummariesByIds(Collection<Long> ids) {
        List<Long> requested = List.copyOf(ids);
        List<WarframeOfficialRecordDTO> result = new ArrayList<>();
        for (int start = 0; start < requested.size(); start += RECORD_BATCH_SIZE) {
            List<Long> batch = requested.subList(start, Math.min(start + RECORD_BATCH_SIZE, requested.size()));
            Specification<T> specification =
                preferredEnabled().and((entity, query, builder) -> entity.get("id").in(batch));
            result.addAll(findBy(specification, query -> query.as(OfficialSummaryDTO.class).all()).stream()
                .map(this::toData).toList());
        }
        return result;
    }

    /**
     * 读取本数据集的轻量名称投影。
     *
     * @return 不含描述长文本与子表的投影。
     */
    default List<OfficialNameDTO> findNames() {
        return findBy(
            preferredEnabled().and((entity, query, builder) -> builder.notEqual(entity.get("bkey"), METADATA_BKEY)),
            query -> query.as(OfficialNameDTO.class).all());
    }

    /**
     * 按业务键真正分批查询，仅装载本次需要的记录。
     *
     * @param keys 已规范化的业务键。
     * @return 自维护优先的摘要，未找到的键不返回。
     */
    default List<WarframeOfficialRecordDTO> findPreferredSummariesByKeys(Collection<String> keys) {
        List<String> requested = keys.stream().map(key -> key.trim().toLowerCase(Locale.ROOT)).distinct().toList();
        List<WarframeOfficialRecordDTO> result = new ArrayList<>();
        for (int start = 0; start < requested.size(); start += RECORD_BATCH_SIZE) {
            List<String> batch = requested.subList(start, Math.min(start + RECORD_BATCH_SIZE, requested.size()));
            Specification<T> spec = preferredEnabled().and((entity, query, builder) -> builder
                .and(builder.lower(entity.get("bkey")).in(batch), builder.notEqual(entity.get("bkey"), METADATA_BKEY)));
            result.addAll(
                findBy(spec, query -> query.as(OfficialSummaryDTO.class).all()).stream().map(this::toData).toList());
        }
        return result;
    }

    /** 同一 bkey 的启用自维护记录覆盖官方整树，比较业务键时忽略大小写。 */
    default Specification<T> preferredEnabled() {
        return (entity, query, builder) -> {
            Subquery<Long> overridden = query.subquery(Long.class);
            Root<? extends T> self = overridden.from(entity.getJavaType());
            overridden.select(self.get("id")).where(builder.isTrue(self.get("enabled")),
                builder.equal(self.get("maintenanceType"), MaintenanceType.SELF_MAINTAINED),
                builder.equal(builder.lower(self.get("bkey")), builder.lower(entity.get("bkey"))));
            return builder.and(builder.isTrue(entity.get("enabled")),
                builder.or(builder.equal(entity.get("maintenanceType"), MaintenanceType.SELF_MAINTAINED),
                    builder.not(builder.exists(overridden))));
        };
    }

    /**
     * 按主键读取非受管业务投影。
     *
     * @param id 目标记录的本地主键。
     * @return 当前独立官方记录的非受管摘要；没有匹配数据时返回空结果。
     */
    default Optional<WarframeOfficialRecordDTO> findDataById(long id) {
        return findBy((entity, query, builder) -> builder.equal(entity.get("id"), id),
            query -> query.as(OfficialSummaryDTO.class).first()).map(this::toData);
    }

    /**
     * 按主键集合批量读取非受管业务投影。
     *
     * @param ids 目标记录标识集合。
     * @return 当前独立官方记录的非受管摘要组成的列表。
     */
    default List<WarframeOfficialRecordDTO> findDataByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids))
            return List.of();
        return findBy((entity, query, builder) -> entity.get("id").in(ids),
            query -> query.as(OfficialSummaryDTO.class).all()).stream().map(this::toData).toList();
    }

    /**
     * 按稳定业务键读取优先使用的自维护或官方记录。
     *
     * @param bkey 当前官方 JSON 记录的稳定业务键。
     * @return 当前独立官方记录的非受管摘要；没有匹配数据时返回空结果。
     */
    default Optional<WarframeOfficialRecordDTO> findPreferredSummary(String bkey) {
        Specification<T> specification = preferredEnabled().and((entity, query, builder) -> builder
            .equal(builder.lower(entity.get("bkey")), bkey.trim().toLowerCase(Locale.ROOT)));
        return findBy(specification, query -> query.as(OfficialSummaryDTO.class).first()).map(this::toData);
    }

    /**
     * 按当前筛选规格读取一页非受管投影。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 包含当前页记录及总数的分页结果。
     */
    default Page<WarframeOfficialRecordDTO> findPage(OfficialRecordQuery request) {
        Specification<T> specification = (entity, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isTrue(entity.get("enabled")));
            // 文件级包头参与导入与审计，但不占用业务列表及分页总数。
            predicates.add(builder.notEqual(entity.get("bkey"), METADATA_BKEY));
            if (StrUtil.isNotBlank(request.group())) {
                String group = request.group().trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                predicates.add(builder.like(entity.get("bkey"), group + "/%", '\\'));
            }
            if (StrUtil.isNotBlank(request.maintenanceType())) {
                predicates.add(
                    builder.equal(entity.get("maintenanceType"), MaintenanceType.valueOf(request.maintenanceType())));
            }
            if (StrUtil.isNotBlank(request.keyword())) {
                String keyword = "%" + request.keyword().toLowerCase(Locale.ROOT).replace("\\", "\\\\")
                    .replace("%", "\\%").replace("_", "\\_") + "%";
                predicates.add(builder.or(builder.like(builder.lower(entity.get("bkey")), keyword, '\\'),
                    builder.like(builder.lower(entity.get("officialName")), keyword, '\\'),
                    builder.like(builder.lower(entity.get("localizedName")), keyword, '\\')));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return findBy(specification, query -> query.as(OfficialSummaryDTO.class).page(request.pageable()))
            .map(this::toData);
    }

    /**
     * 统计指定维护类别的启用业务记录，排除文件级包头。
     *
     * @param maintenanceType 官方或自维护类别。
     * @return 当前数据集可在管理列表中展示的记录数。
     */
    default long countBusinessRecords(MaintenanceType maintenanceType) {
        return count((entity, query, builder) -> builder.and(builder.isTrue(entity.get("enabled")),
            builder.notEqual(entity.get("bkey"), METADATA_BKEY),
            builder.equal(entity.get("maintenanceType"), maintenanceType)));
    }

    private WarframeOfficialRecordDTO toData(OfficialSummaryDTO projection) {
        WarframeOfficialRecordDTO data = projection.toData();
        data.setProductPath(domain().productPath());
        data.setDomainCode(domain().domainCode());
        return data;
    }

    /**
     * 显式保存当前独立主表的业务记录。
     *
     * @param record 当前业务记录。
     * @return 目标类型对象。
     */
    @SuppressWarnings("unchecked")
    default T saveRecord(OfficialRecord record) {
        if (!newRecord().getClass().isInstance(record)) {
            throw new IllegalArgumentException("Entity does not belong to dataset " + domain());
        }
        return save((T)record);
    }
}
