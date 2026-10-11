package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * WorldState 语言词条。language_key 保存上游引用，中英合并列供运行时索引读取。
 * current_json 的 en/zh 对象保留 value/desc；刷新时按字段选择中文，不能用整对象覆盖英文兜底。
 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_worldstate_language")
@AttributeOverride(name = "recordKey", column = @Column(name = "language_key", nullable = false, length = 500))
public class WarframeWorldStateLanguage extends WarframeCommunityDatasetRecord {
    private String locale;
    @Column(name = "display_text")
    private String displayText;
    @Column(name = "display_text_zh")
    private String displayTextZh;
    @Column(name = "display_text_en")
    private String displayTextEn;
    public String getLanguageKey() {
        return getRecordKey();
    }

    public void setLanguageKey(String languageKey) {
        setRecordKey(languageKey);
    }
}
