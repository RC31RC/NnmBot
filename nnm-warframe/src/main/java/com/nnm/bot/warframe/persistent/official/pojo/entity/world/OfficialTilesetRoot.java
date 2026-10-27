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

/** 源 TILESET 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_tileset_root")
public class OfficialTilesetRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 阵营是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "faction_state")
    private Short factionState;

    /** 阵营。 */
    @Column(name = "faction", columnDefinition = "TEXT")
    private String faction;

    /** 阵营英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "factionnameen_state")
    private Short factionNameEnState;

    /** 阵营英文名称。 */
    @Column(name = "factionnameen", columnDefinition = "TEXT")
    private String factionNameEn;

    /** 阵营中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "factionnamezh_state")
    private Short factionNameZhState;

    /** 阵营中文名称。 */
    @Column(name = "factionnamezh", columnDefinition = "TEXT")
    private String factionNameZh;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 任务定义列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missiondefinitions_state")
    private Short missionDefinitionsState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTilesetMissionDefinitionsItems> officialTilesetMissionDefinitionsItemsRows = new ArrayList<>();
}
