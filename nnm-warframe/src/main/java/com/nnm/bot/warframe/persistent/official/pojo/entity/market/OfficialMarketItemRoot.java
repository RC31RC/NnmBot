package com.nnm.bot.warframe.persistent.official.pojo.entity.market;

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

/** 源 MARKET_ITEM 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_market_item_root")
public class OfficialMarketItemRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 基础内融核心价值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "baseendo_state")
    private Short baseEndoState;

    /** 基础内融核心价值。 */
    @Column(name = "baseendo", columnDefinition = "NUMERIC")
    private BigDecimal baseEndo;

    /** 是否支持批量交易是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bulktradable_state")
    private Short bulkTradableState;

    /** 是否支持批量交易。 */
    @Column(name = "bulktradable")
    private Boolean bulkTradable;

    /** 杜卡德金币数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "ducats_state")
    private Short ducatsState;

    /** 杜卡德金币数量。 */
    @Column(name = "ducats", columnDefinition = "NUMERIC")
    private BigDecimal ducats;

    /** 内融核心倍率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "endomultiplier_state")
    private Short endoMultiplierState;

    /** 内融核心倍率。 */
    @Column(name = "endomultiplier", columnDefinition = "NUMERIC")
    private BigDecimal endoMultiplier;

    /** 游戏物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_state")
    private Short gameItemState;

    /** 游戏物品的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_iconstatus_state")
    private Short gameItemIconStatusState;

    /** 游戏物品的图标状态。 */
    @Column(name = "gameitem_iconstatus", columnDefinition = "TEXT")
    private String gameItemIconStatus;

    /** 游戏物品的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_iconurl_state")
    private Short gameItemIconUrlState;

    /** 游戏物品的图标地址。 */
    @Column(name = "gameitem_iconurl", columnDefinition = "TEXT")
    private String gameItemIconUrl;

    /** 游戏物品的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_name_state")
    private Short gameItemNameState;

    /** 游戏物品的名称。 */
    @Column(name = "gameitem_name", columnDefinition = "TEXT")
    private String gameItemName;

    /** 游戏物品的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_nameen_state")
    private Short gameItemNameEnState;

    /** 游戏物品的英文名称。 */
    @Column(name = "gameitem_nameen", columnDefinition = "TEXT")
    private String gameItemNameEn;

    /** 游戏物品的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_namezh_state")
    private Short gameItemNameZhState;

    /** 游戏物品的中文名称。 */
    @Column(name = "gameitem_namezh", columnDefinition = "TEXT")
    private String gameItemNameZh;

    /** 游戏物品的解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_resolution_state")
    private Short gameItemResolutionState;

    /** 游戏物品的解析状态。 */
    @Column(name = "gameitem_resolution", columnDefinition = "TEXT")
    private String gameItemResolution;

    /** 游戏物品的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameitem_uniquename_state")
    private Short gameItemUniqueNameState;

    /** 游戏物品的唯一标识。 */
    @Column(name = "gameitem_uniquename", columnDefinition = "TEXT")
    private String gameItemUniqueName;

    /** 游戏引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameref_state")
    private Short gameRefState;

    /** 游戏引用。 */
    @Column(name = "gameref", columnDefinition = "TEXT")
    private String gameRef;

    /** 游戏引用状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gamerefstatus_state")
    private Short gameRefStatusState;

    /** 游戏引用状态。 */
    @Column(name = "gamerefstatus", columnDefinition = "TEXT")
    private String gameRefStatus;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

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

    /** 标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "id_value_state")
    private Short idState;

    /** 标识。 */
    @Column(name = "id_value", columnDefinition = "TEXT")
    private String idValue;

    /** 琥珀星最大镶嵌数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxamberstars_state")
    private Short maxAmberStarsState;

    /** 琥珀星最大镶嵌数量。 */
    @Column(name = "maxamberstars", columnDefinition = "NUMERIC")
    private BigDecimal maxAmberStars;

    /** 青蓝星最大镶嵌数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxcyanstars_state")
    private Short maxCyanStarsState;

    /** 青蓝星最大镶嵌数量。 */
    @Column(name = "maxcyanstars", columnDefinition = "NUMERIC")
    private BigDecimal maxCyanStars;

    /** 最高等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxrank_state")
    private Short maxRankState;

    /** 最高等级。 */
    @Column(name = "maxrank", columnDefinition = "NUMERIC")
    private BigDecimal maxRank;

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

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slug_state")
    private Short slugState;

    /** 网址标识。 */
    @Column(name = "slug", columnDefinition = "TEXT")
    private String slug;

    /** 次级图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "subicon_state")
    private Short subIconState;

    /** 次级图标。 */
    @Column(name = "subicon", columnDefinition = "TEXT")
    private String subIcon;

    /** 子类型列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "subtypes_state")
    private Short subtypesState;

    /** 标签列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tags_state")
    private Short tagsState;

    /** 缩略图是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "thumb_state")
    private Short thumbState;

    /** 缩略图。 */
    @Column(name = "thumb", columnDefinition = "TEXT")
    private String thumb;

    /** 缩略图地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "thumburl_state")
    private Short thumbUrlState;

    /** 缩略图地址。 */
    @Column(name = "thumburl", columnDefinition = "TEXT")
    private String thumbUrl;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "url_name_state")
    private Short urlNameState;

    /** 网址标识。 */
    @Column(name = "url_name", columnDefinition = "TEXT")
    private String urlName;

    /** 是否已入库是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "vaulted_state")
    private Short vaultedState;

    /** 是否已入库。 */
    @Column(name = "vaulted")
    private Boolean vaulted;

    /** 中文图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhicon_state")
    private Short zhIconState;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhname_state")
    private Short zhNameState;

    /** 中文名称。 */
    @Column(name = "zhname", columnDefinition = "TEXT")
    private String zhName;

    /** 中文次级图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhsubicon_state")
    private Short zhSubIconState;

    /** 中文缩略图是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhthumb_state")
    private Short zhThumbState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialMarketItemSubtypesItems> officialMarketItemSubtypesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialMarketItemTagsItems> officialMarketItemTagsItemsRows = new ArrayList<>();
}
