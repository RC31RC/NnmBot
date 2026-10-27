package com.nnm.bot.warframe.persistent.official.pojo.entity.equipment;

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

/** 源 WEAPON defaultUpgrades/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_weapon_defaultupgrades_items")
public class OfficialWeaponDefaultUpgradesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 物品类型路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemtype_state")
    private Short itemTypeState;

    /** 物品类型路径。 */
    @Column(name = "itemtype", columnDefinition = "TEXT")
    private String itemType;

    /** 槽位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slot_state")
    private Short slotState;

    /** 槽位。 */
    @Column(name = "slot", columnDefinition = "NUMERIC")
    private BigDecimal slot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialWeaponRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponDefaultUpgradesItemsItemReferencesItems> officialWeaponDefaultUpgradesItemsItemReferencesItemsRows = new ArrayList<>();
}
