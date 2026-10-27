package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

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

/** 源 TEXT_ICON 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_text_icon_root")
public class OfficialTextIconRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 通用平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_agnostic_state")
    private Short ditAgnosticState;

    /** 通用平台。 */
    @Column(name = "dit_agnostic", columnDefinition = "TEXT")
    private String ditAgnostic;

    /** 安卓平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_android_state")
    private Short ditAndroidState;

    /** 安卓平台。 */
    @Column(name = "dit_android", columnDefinition = "TEXT")
    private String ditAndroid;

    /** 自动平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_auto_state")
    private Short ditAutoState;

    /** 自动平台。 */
    @Column(name = "dit_auto", columnDefinition = "TEXT")
    private String ditAuto;

    /** iOS 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_ios_state")
    private Short ditIosState;

    /** iOS 平台。 */
    @Column(name = "dit_ios", columnDefinition = "TEXT")
    private String ditIos;

    /** 电脑平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_pc_state")
    private Short ditPcState;

    /** 电脑平台。 */
    @Column(name = "dit_pc", columnDefinition = "TEXT")
    private String ditPc;

    /** PlayStation 4 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_ps4_state")
    private Short ditPs4State;

    /** PlayStation 4 平台。 */
    @Column(name = "dit_ps4", columnDefinition = "TEXT")
    private String ditPs4;

    /** PlayStation 5 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_ps5_state")
    private Short ditPs5State;

    /** PlayStation 5 平台。 */
    @Column(name = "dit_ps5", columnDefinition = "TEXT")
    private String ditPs5;

    /** Steam 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_steam_state")
    private Short ditSteamState;

    /** Steam 平台。 */
    @Column(name = "dit_steam", columnDefinition = "TEXT")
    private String ditSteam;

    /** Switch 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_switch_state")
    private Short ditSwitchState;

    /** Switch 平台。 */
    @Column(name = "dit_switch", columnDefinition = "TEXT")
    private String ditSwitch;

    /** Xbox 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dit_xbone_state")
    private Short ditXboneState;

    /** Xbox 平台。 */
    @Column(name = "dit_xbone", columnDefinition = "TEXT")
    private String ditXbone;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTextIconItemReferencesItems> officialTextIconItemReferencesItemsRows = new ArrayList<>();
}
