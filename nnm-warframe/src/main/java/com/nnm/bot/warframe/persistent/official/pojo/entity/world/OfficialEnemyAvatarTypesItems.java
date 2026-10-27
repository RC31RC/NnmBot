package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

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

/** 源 ENEMY avatarTypes/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_enemy_avatartypes_items")
public class OfficialEnemyAvatarTypesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 引用的领域是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_domain_state")
    private Short referenceDomainState;

    /** 引用的领域。 */
    @Column(name = "reference_domain", columnDefinition = "TEXT")
    private String referenceDomain;

    /** 引用的字段是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_field_state")
    private Short referenceFieldState;

    /** 引用的字段。 */
    @Column(name = "reference_field", columnDefinition = "TEXT")
    private String referenceField;

    /** 引用的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_iconstatus_state")
    private Short referenceIconStatusState;

    /** 引用的图标状态。 */
    @Column(name = "reference_iconstatus", columnDefinition = "TEXT")
    private String referenceIconStatus;

    /** 引用的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_name_state")
    private Short referenceNameState;

    /** 引用的名称。 */
    @Column(name = "reference_name", columnDefinition = "TEXT")
    private String referenceName;

    /** 引用的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_nameen_state")
    private Short referenceNameEnState;

    /** 引用的英文名称。 */
    @Column(name = "reference_nameen", columnDefinition = "TEXT")
    private String referenceNameEn;

    /** 引用的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_namezh_state")
    private Short referenceNameZhState;

    /** 引用的中文名称。 */
    @Column(name = "reference_namezh", columnDefinition = "TEXT")
    private String referenceNameZh;

    /** 引用的引用类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_referencekind_state")
    private Short referenceReferenceKindState;

    /** 引用的引用类型。 */
    @Column(name = "reference_referencekind", columnDefinition = "TEXT")
    private String referenceReferenceKind;

    /** 引用的解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_resolution_state")
    private Short referenceResolutionState;

    /** 引用的解析状态。 */
    @Column(name = "reference_resolution", columnDefinition = "TEXT")
    private String referenceResolution;

    /** 引用的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reference_uniquename_state")
    private Short referenceUniqueNameState;

    /** 引用的唯一标识。 */
    @Column(name = "reference_uniquename", columnDefinition = "TEXT")
    private String referenceUniqueName;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 变体是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "variant_state")
    private Short variantState;

    /** 变体。 */
    @Column(name = "variant", columnDefinition = "TEXT")
    private String variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialEnemyRoot parent;
}
