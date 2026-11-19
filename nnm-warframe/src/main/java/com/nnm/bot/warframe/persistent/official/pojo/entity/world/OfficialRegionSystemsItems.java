package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 REGION systems/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_region_systems_items")
public class OfficialRegionSystemsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 伤害几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "damagechance_state")
    private Short damageChanceState;

    /** 伤害几率。 */
    @Column(name = "damagechance", columnDefinition = "NUMERIC")
    private BigDecimal damageChance;

    /** 采集无人机所受伤害的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dronedamage_maxvalue_state")
    private Short droneDamageMaxValueState;

    /** 采集无人机所受伤害的最大值。 */
    @Column(name = "dronedamage_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal droneDamageMaxValue;

    /** 采集无人机所受伤害的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dronedamage_minvalue_state")
    private Short droneDamageMinValueState;

    /** 采集无人机所受伤害的最小值。 */
    @Column(name = "dronedamage_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal droneDamageMinValue;

    /** 序号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "index_value_state")
    private Short indexState;

    /** 序号。 */
    @Column(name = "index_value", columnDefinition = "NUMERIC")
    private BigDecimal index;

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

    /** 资源列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resources_state")
    private Short resourcesState;

    /** 星系序号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemindex_state")
    private Short systemIndexState;

    /** 星系序号。 */
    @Column(name = "systemindex", columnDefinition = "NUMERIC")
    private BigDecimal systemIndex;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhname_state")
    private Short zhNameState;

    /** 中文名称。 */
    @Column(name = "zhname", columnDefinition = "TEXT")
    private String zhName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRegionRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionSystemsItemsResourcesItems> officialRegionSystemsItemsResourcesItemsRows = new ArrayList<>();
}
