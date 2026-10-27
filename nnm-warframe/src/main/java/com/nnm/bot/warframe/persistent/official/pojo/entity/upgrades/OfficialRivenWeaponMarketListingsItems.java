package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 RIVEN_WEAPON marketListings/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_riven_weapon_marketlistings_items")
public class OfficialRivenWeaponMarketListingsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 倾向值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "disposition_state")
    private Short dispositionState;

    /** 倾向值。 */
    @Column(name = "disposition", columnDefinition = "NUMERIC")
    private BigDecimal disposition;

    /** 游戏引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameref_state")
    private Short gameRefState;

    /** 游戏引用。 */
    @Column(name = "gameref", columnDefinition = "TEXT")
    private String gameRef;

    /** 组是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "group_value_state")
    private Short groupState;

    /** 组。 */
    @Column(name = "group_value", columnDefinition = "TEXT")
    private String group;

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

    /** 所需段位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reqmasteryrank_state")
    private Short reqMasteryRankState;

    /** 所需段位。 */
    @Column(name = "reqmasteryrank", columnDefinition = "NUMERIC")
    private BigDecimal reqMasteryRank;

    /** 裂罅类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "riventype_state")
    private Short rivenTypeState;

    /** 裂罅类型。 */
    @Column(name = "riventype", columnDefinition = "TEXT")
    private String rivenType;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slug_state")
    private Short slugState;

    /** 网址标识。 */
    @Column(name = "slug", columnDefinition = "TEXT")
    private String slug;

    /** 缩略图是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "thumb_state")
    private Short thumbState;

    /** 缩略图。 */
    @Column(name = "thumb", columnDefinition = "TEXT")
    private String thumb;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "url_name_state")
    private Short urlNameState;

    /** 网址标识。 */
    @Column(name = "url_name", columnDefinition = "TEXT")
    private String urlName;

    /** 中文图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhicon_state")
    private Short zhIconState;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhname_state")
    private Short zhNameState;

    /** 中文名称。 */
    @Column(name = "zhname", columnDefinition = "TEXT")
    private String zhName;

    /** 中文缩略图是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhthumb_state")
    private Short zhThumbState;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRivenWeaponRoot parent;
}
