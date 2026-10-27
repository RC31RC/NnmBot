package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 源 LANGUAGE_STRING 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_language_string_root")
public class OfficialLanguageStringRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "key_value_state")
    private Short keyState;

    /** 键。 */
    @Column(name = "key_value", columnDefinition = "TEXT")
    private String key;

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

}
