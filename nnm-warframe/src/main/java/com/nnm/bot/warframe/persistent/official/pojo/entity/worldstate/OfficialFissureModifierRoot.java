package com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * 裂缝等级代码簿。按等级代码解析遗物阶级和裂缝名称；节点、任务和时间来自动态活动。 同一业务键可保留官方和自维护版本。由生成器和契约测试共同维护。
 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_fissure_modifier_root")
public class OfficialFissureModifierRoot extends OfficialRecord {
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

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 阶级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tier_state")
    private Short tierState;

    /** 阶级。 */
    @Column(name = "tier", columnDefinition = "NUMERIC")
    private BigDecimal tier;

}
