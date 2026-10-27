package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 导入时按 JSON 数据集定位具体主表 Repository。它是导入/通用读取使用的注册表，不是聚合实体、统一 Repository 或数据库表。 */
@Component
public class OfficialRepositoryRegistry {
    @Resource
    private List<OfficialMainRepository<?>> repositories;

    private Map<WarframeOfficialDataDomain, OfficialMainRepository<?>> byDomain;
    private Map<Class<?>, OfficialMainRepository<?>> byEntityType;

    /**
     * 初始化组件在运行期间复用的状态。
     */
    @PostConstruct
    void initialize() {
        Map<WarframeOfficialDataDomain, OfficialMainRepository<?>> indexed =
                new EnumMap<>(WarframeOfficialDataDomain.class);
        Map<Class<?>, OfficialMainRepository<?>> entityTypes = new HashMap<>();
        for (OfficialMainRepository<?> repository : repositories) {
            if (indexed.put(repository.domain(), repository) != null) {
                throw new IllegalStateException("Duplicate official repository: " + repository.domain());
            }
            if (entityTypes.put(repository.newRecord().getClass(), repository) != null) {
                throw new IllegalStateException("Duplicate official entity type: " + repository.domain());
            }
        }
        for (WarframeOfficialDataDomain domain : WarframeOfficialDataDomain.values()) {
            if (domain != WarframeOfficialDataDomain.OTHER && !indexed.containsKey(domain)) {
                throw new IllegalStateException("Missing main-table repository: " + domain);
            }
        }
        byDomain = Collections.unmodifiableMap(indexed);
        byEntityType = Collections.unmodifiableMap(entityTypes);
    }

    /**
     * 取得已登记且满足当前业务约束的对象，缺失时直接失败。
     *
     * @param domain 本次操作所属的数据文件或社区资料类型。
     * @return 官方主表数据访问对象。
     */
    public OfficialMainRepository<?> require(WarframeOfficialDataDomain domain) {
        OfficialMainRepository<?> repository = byDomain.get(domain);
        if (repository == null) throw new IllegalArgumentException("Unsupported domain: " + domain);
        return repository;
    }

    /**
     * 按独立主表的具体实体类型取得其固定领域 Repository。
     *
     * @param entity 已加载或待持久化的官方记录。
     * @return 与该实体类型唯一对应的主表 Repository。
     */
    public OfficialMainRepository<?> require(OfficialRecord entity) {
        OfficialMainRepository<?> repository = byEntityType.get(entity.getClass());
        if (repository == null) throw new IllegalArgumentException("Unsupported official record: " + entity.getClass());
        return repository;
    }

    /**
     * 读取当前业务登记的全部对象。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<OfficialMainRepository<?>> all() {
        return List.copyOf(byDomain.values());
    }
}
