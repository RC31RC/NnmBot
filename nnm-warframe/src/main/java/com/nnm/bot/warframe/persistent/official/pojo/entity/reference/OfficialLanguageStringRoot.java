package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 游戏语言键到双语文本的字典。用完整语言键解析名称和说明；翻译文本本身不承担物品身份。 同一业务键可保留官方和自维护版本。由生成器和契约测试共同维护。
 */
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
