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

/** 源 TILESET missionDefinitions/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_tileset_missiondefinitions_items")
public class OfficialTilesetMissionDefinitionsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 高级敌人生成器列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "advancedspawners_state")
    private Short advancedSpawnersState;

    /** 敌人配置列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "enemyspecs_state")
    private Short enemySpecsState;

    /** 额外敌人配置列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "extraenemyspecs_state")
    private Short extraEnemySpecsState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 任务类型代码是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missiontype_state")
    private Short missionTypeState;

    /** 任务类型代码。 */
    @Column(name = "missiontype", columnDefinition = "TEXT")
    private String missionType;

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

    /** 异常状态等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "proclevel_state")
    private Short procLevelState;

    /** 异常状态等级。 */
    @Column(name = "proclevel", columnDefinition = "TEXT")
    private String procLevel;

    /** 重点目标配置是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "vipagent_state")
    private Short vipAgentState;

    /** 重点目标配置。 */
    @Column(name = "vipagent", columnDefinition = "TEXT")
    private String vipAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialTilesetRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems> officialTilesetMissionDefinitionsItemsAdvancedSpawnersItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTilesetMissionDefinitionsItemsEnemySpecsItems> officialTilesetMissionDefinitionsItemsEnemySpecsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems> officialTilesetMissionDefinitionsItemsExtraEnemySpecsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialTilesetMissionDefinitionsItemsItemReferencesItems> officialTilesetMissionDefinitionsItemsItemReferencesItemsRows = new ArrayList<>();
}
