package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

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

/** 源 RIVEN_WEAPON 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_riven_weapon_root")
public class OfficialRivenWeaponRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 倾向值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "disposition_state")
    private Short dispositionState;

    /** 倾向值。 */
    @Column(name = "disposition", columnDefinition = "NUMERIC")
    private BigDecimal disposition;

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

    /** 数据质量统计的已收录倾向值的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_dispositioncount_state")
    private Short qualityDispositionCountState;

    /** 数据质量统计的已收录倾向值的数量。 */
    @Column(name = "quality_dispositioncount", columnDefinition = "NUMERIC")
    private BigDecimal qualityDispositionCount;

    /** 数据质量统计的市场商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_marketlistingcount_state")
    private Short qualityMarketListingCountState;

    /** 数据质量统计的市场商品数量。 */
    @Column(name = "quality_marketlistingcount", columnDefinition = "NUMERIC")
    private BigDecimal qualityMarketListingCount;

    /** 数据质量统计的已关联的市场商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_marketlistingsattached_state")
    private Short qualityMarketListingsAttachedState;

    /** 数据质量统计的已关联的市场商品数量。 */
    @Column(name = "quality_marketlistingsattached", columnDefinition = "NUMERIC")
    private BigDecimal qualityMarketListingsAttached;

    /** 数据质量统计的包含倾向值的市场商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_marketlistingswithdisposition_state")
    private Short qualityMarketListingsWithDispositionState;

    /** 数据质量统计的包含倾向值的市场商品数量。 */
    @Column(name = "quality_marketlistingswithdisposition", columnDefinition = "NUMERIC")
    private BigDecimal qualityMarketListingsWithDisposition;

    /** 数据质量统计的缺少倾向值的市场商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_marketlistingswithoutdisposition_state")
    private Short qualityMarketListingsWithoutDispositionState;

    /** 数据质量统计的缺少倾向值的市场商品数量。 */
    @Column(name = "quality_marketlistingswithoutdisposition", columnDefinition = "NUMERIC")
    private BigDecimal qualityMarketListingsWithoutDisposition;

    /** 数据质量统计的未解析的市场武器列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_unresolvedmarketweapons_state")
    private Short qualityUnresolvedMarketWeaponsState;

    /** 数据质量统计的未解析的市场武器列表。 */
    @Column(name = "quality_unresolvedmarketweapons", columnDefinition = "NUMERIC")
    private BigDecimal qualityUnresolvedMarketWeapons;

    /** 数据质量统计的武器数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_weaponcount_state")
    private Short qualityWeaponCountState;

    /** 数据质量统计的武器数量。 */
    @Column(name = "quality_weaponcount", columnDefinition = "NUMERIC")
    private BigDecimal qualityWeaponCount;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 未解析的市场武器列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unresolvedmarketweapons_state")
    private Short unresolvedMarketWeaponsState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenWeaponMarketListingsItems> officialRivenWeaponMarketListingsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenWeaponUnresolvedMarketWeaponsItems> officialRivenWeaponUnresolvedMarketWeaponsItemsRows = new ArrayList<>();
}
