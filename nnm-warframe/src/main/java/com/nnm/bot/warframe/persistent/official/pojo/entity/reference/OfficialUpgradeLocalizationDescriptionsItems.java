package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

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

/** 源 UPGRADE_LOCALIZATION descriptions/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_localization_descriptions_items")
public class OfficialUpgradeLocalizationDescriptionsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 文本行序号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "lineindex_state")
    private Short lineIndexState;

    /** 文本行序号。 */
    @Column(name = "lineindex", columnDefinition = "NUMERIC")
    private BigDecimal lineIndex;

    /** 文本是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "text_state")
    private Short textState;

    /** 文本。 */
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    /** 中文文本是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhtext_state")
    private Short zhTextState;

    /** 中文文本。 */
    @Column(name = "zhtext", columnDefinition = "TEXT")
    private String zhText;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialUpgradeLocalizationRoot parent;
}
