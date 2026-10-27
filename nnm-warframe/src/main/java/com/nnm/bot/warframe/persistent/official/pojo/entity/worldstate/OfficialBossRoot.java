package com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate;

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

/** 源 BOSS 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_boss_root")
public class OfficialBossRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

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

    /** 名称本地化键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namekey_state")
    private Short nameKeyState;

    /** 名称本地化键。 */
    @Column(name = "namekey", columnDefinition = "TEXT")
    private String nameKey;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 执刑官源力石分类的选择项是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "shardcategory_selection_state")
    private Short shardCategorySelectionState;

    /** 执刑官源力石分类的选择项。 */
    @Column(name = "shardcategory_selection", columnDefinition = "TEXT")
    private String shardCategorySelection;

    /** 执刑官源力石分类的变体列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "shardcategory_variants_state")
    private Short shardCategoryVariantsState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBossShardCategoryVariantsItems> officialBossShardCategoryVariantsItemsRows = new ArrayList<>();
}
