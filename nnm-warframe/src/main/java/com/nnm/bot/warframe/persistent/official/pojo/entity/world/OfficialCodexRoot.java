package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

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

/** 源 CODEX 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_codex_root")
public class OfficialCodexRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

    /** 英文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionen_state")
    private Short descriptionEnState;

    /** 英文描述。 */
    @Column(name = "descriptionen", columnDefinition = "TEXT")
    private String descriptionEn;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconasset_state")
    private Short iconAssetState;

    /** 图标资源。 */
    @Column(name = "iconasset", columnDefinition = "TEXT")
    private String iconAsset;

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

    /** 图片是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "image_state")
    private Short imageState;

    /** 图片。 */
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

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

    /** 所需扫描次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reqscans_state")
    private Short reqScansState;

    /** 所需扫描次数。 */
    @Column(name = "reqscans", columnDefinition = "NUMERIC")
    private BigDecimal reqScans;

    /** 隐藏内容是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secret_state")
    private Short secretState;

    /** 隐藏内容。 */
    @Column(name = "secret")
    private Boolean secret;

    /** 隐藏通讯的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secrettransmission_itemreferences_state")
    private Short secretTransmissionItemReferencesState;

    /** 隐藏通讯的声音资源路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secrettransmission_sound_state")
    private Short secretTransmissionSoundState;

    /** 隐藏通讯的声音资源路径。 */
    @Column(name = "secrettransmission_sound", columnDefinition = "TEXT")
    private String secretTransmissionSound;

    /** 隐藏通讯的文本是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secrettransmission_text_state")
    private Short secretTransmissionTextState;

    /** 隐藏通讯的文本。 */
    @Column(name = "secrettransmission_text", columnDefinition = "TEXT")
    private String secretTransmissionText;

    /** 隐藏通讯的英文文本是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secrettransmission_texten_state")
    private Short secretTransmissionTextEnState;

    /** 隐藏通讯的英文文本。 */
    @Column(name = "secrettransmission_texten", columnDefinition = "TEXT")
    private String secretTransmissionTextEn;

    /** 隐藏通讯的中文文本是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secrettransmission_textzh_state")
    private Short secretTransmissionTextZhState;

    /** 隐藏通讯的中文文本。 */
    @Column(name = "secrettransmission_textzh", columnDefinition = "TEXT")
    private String secretTransmissionTextZh;

    /** 歌曲是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "song_state")
    private Short songState;

    /** 歌曲。 */
    @Column(name = "song", columnDefinition = "TEXT")
    private String song;

    /** 战甲是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "suit_state")
    private Short suitState;

    /** 战甲。 */
    @Column(name = "suit", columnDefinition = "TEXT")
    private String suit;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialCodexItemReferencesItems> officialCodexItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialCodexSecretTransmissionItemReferencesItems> officialCodexSecretTransmissionItemReferencesItemsRows = new ArrayList<>();
}
