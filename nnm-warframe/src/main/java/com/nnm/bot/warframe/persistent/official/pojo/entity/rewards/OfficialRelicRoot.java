package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 RELIC 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_root")
public class OfficialRelicRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconurl_state")
    private Short iconUrlState;

    /** 图标地址。 */
    @Column(name = "iconurl", columnDefinition = "TEXT")
    private String iconUrl;

    /** 获取地点列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "locations_state")
    private Short locationsState;

    /** 关联市场商品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "marketlistings_state")
    private Short marketListingsState;

    /** 名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "name_state")
    private Short nameState;

    /** 名称。 */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /** 英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nameen_state")
    private Short nameEnState;

    /** 英文名称。 */
    @Column(name = "nameen", columnDefinition = "TEXT")
    private String nameEn;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 数据质量统计的去重后的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_deduplicatedrows_state")
    private Short qualityDeduplicatedRowsState;

    /** 数据质量统计的去重后的记录数。 */
    @Column(name = "quality_deduplicatedrows", columnDefinition = "NUMERIC")
    private BigDecimal qualityDeduplicatedRows;

    /** 数据质量统计的掉落表记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_droptablerows_state")
    private Short qualityDropTableRowsState;

    /** 数据质量统计的掉落表记录数。 */
    @Column(name = "quality_droptablerows", columnDefinition = "NUMERIC")
    private BigDecimal qualityDropTableRows;

    /** 数据质量统计的推断出的来源分组数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_inferredsourcegroups_state")
    private Short qualityInferredSourceGroupsState;

    /** 数据质量统计的推断出的来源分组数。 */
    @Column(name = "quality_inferredsourcegroups", columnDefinition = "NUMERIC")
    private BigDecimal qualityInferredSourceGroups;

    /** 数据质量统计的已匹配的掉落记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_matcheddroprows_state")
    private Short qualityMatchedDropRowsState;

    /** 数据质量统计的已匹配的掉落记录数。 */
    @Column(name = "quality_matcheddroprows", columnDefinition = "NUMERIC")
    private BigDecimal qualityMatchedDropRows;

    /** 数据质量统计的任务地点记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_missionlocationrows_state")
    private Short qualityMissionLocationRowsState;

    /** 数据质量统计的任务地点记录数。 */
    @Column(name = "quality_missionlocationrows", columnDefinition = "NUMERIC")
    private BigDecimal qualityMissionLocationRows;

    /** 数据质量统计的官方精炼状态数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_officialstatecount_state")
    private Short qualityOfficialStateCountState;

    /** 数据质量统计的官方精炼状态数量。 */
    @Column(name = "quality_officialstatecount", columnDefinition = "NUMERIC")
    private BigDecimal qualityOfficialStateCount;

    /** 数据质量统计的遗物数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reliccount_state")
    private Short qualityRelicCountState;

    /** 数据质量统计的遗物数量。 */
    @Column(name = "quality_reliccount", columnDefinition = "NUMERIC")
    private BigDecimal qualityRelicCount;

    /** 数据质量统计的遗物来源记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_relicsourcerows_state")
    private Short qualityRelicSourceRowsState;

    /** 数据质量统计的遗物来源记录数。 */
    @Column(name = "quality_relicsourcerows", columnDefinition = "NUMERIC")
    private BigDecimal qualityRelicSourceRows;

    /** 数据质量统计的未解析的掉落记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_unresolveddroprows_state")
    private Short qualityUnresolvedDropRowsState;

    /** 数据质量统计的未解析的掉落记录数。 */
    @Column(name = "quality_unresolveddroprows", columnDefinition = "NUMERIC")
    private BigDecimal qualityUnresolvedDropRows;

    /** 数据质量统计的未解析的地点记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_unresolvedlocationrows_state")
    private Short qualityUnresolvedLocationRowsState;

    /** 数据质量统计的未解析的地点记录数。 */
    @Column(name = "quality_unresolvedlocationrows", columnDefinition = "NUMERIC")
    private BigDecimal qualityUnresolvedLocationRows;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 遗物代码是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reliccode_state")
    private Short relicCodeState;

    /** 遗物代码。 */
    @Column(name = "reliccode", columnDefinition = "TEXT")
    private String relicCode;

    /** 状态集合是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "states_state")
    private Short statesState;

    /** 阶级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tier_state")
    private Short tierState;

    /** 阶级。 */
    @Column(name = "tier", columnDefinition = "NUMERIC")
    private BigDecimal tier;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 未解析的掉落记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unresolveddroprows_state")
    private Short unresolvedDropRowsState;

    /** 未解析的地点记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unresolvedlocationrows_state")
    private Short unresolvedLocationRowsState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicLocationsItems> officialRelicLocationsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicMarketListingsItems> officialRelicMarketListingsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntries> officialRelicStatesEntriesRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicUnresolvedDropRowsItems> officialRelicUnresolvedDropRowsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicUnresolvedLocationRowsItems> officialRelicUnresolvedLocationRowsItemsRows = new ArrayList<>();
}
