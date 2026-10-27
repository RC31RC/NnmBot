package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

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

/** 源 BOUNTY stages/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_bounty_stages_items")
public class OfficialBountyStagesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 启动概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "activationprobability_state")
    private Short activationProbabilityState;

    /** 启动概率。 */
    @Column(name = "activationprobability", columnDefinition = "NUMERIC")
    private BigDecimal activationProbability;

    /** 分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "category_state")
    private Short categoryState;

    /** 分类。 */
    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    /** 抽取次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawcount_state")
    private Short drawCountState;

    /** 抽取次数。 */
    @Column(name = "drawcount", columnDefinition = "NUMERIC")
    private BigDecimal drawCount;

    /** 抽取方式是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawmode_state")
    private Short drawModeState;

    /** 抽取方式。 */
    @Column(name = "drawmode", columnDefinition = "TEXT")
    private String drawMode;

    /** 分组列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "groups_state")
    private Short groupsState;

    /** 奖池标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "poolid_state")
    private Short poolIdState;

    /** 奖池标识。 */
    @Column(name = "poolid", columnDefinition = "TEXT")
    private String poolId;

    /** 奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewards_state")
    private Short rewardsState;

    /** 来源信息的身份解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_identityresolution_state")
    private Short sourceIdentityResolutionState;

    /** 来源信息的身份解析状态。 */
    @Column(name = "source_identityresolution", columnDefinition = "TEXT")
    private String sourceIdentityResolution;

    /** 来源信息的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_name_state")
    private Short sourceNameState;

    /** 来源信息的名称。 */
    @Column(name = "source_name", columnDefinition = "TEXT")
    private String sourceName;

    /** 来源信息的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_nameen_state")
    private Short sourceNameEnState;

    /** 来源信息的英文名称。 */
    @Column(name = "source_nameen", columnDefinition = "TEXT")
    private String sourceNameEn;

    /** 来源信息的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_namezh_state")
    private Short sourceNameZhState;

    /** 来源信息的中文名称。 */
    @Column(name = "source_namezh", columnDefinition = "TEXT")
    private String sourceNameZh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialBountyRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBountyStagesItemsGroupsItems> officialBountyStagesItemsGroupsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBountyStagesItemsRewardsItems> officialBountyStagesItemsRewardsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBountyStagesItemsItems> officialBountyStagesItemsItemsRows = new ArrayList<>();
}
