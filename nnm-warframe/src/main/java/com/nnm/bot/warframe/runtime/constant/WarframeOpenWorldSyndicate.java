package com.nnm.bot.warframe.runtime.constant;

/** 平原赏金以官方集团代码精确归属，避免英择谛与科维兽混淆。 */
public enum WarframeOpenWorldSyndicate {
    CETUS("CetusSyndicate", "ostrons", "希图斯"), SOLARIS("SolarisSyndicate", "solaris", "索拉里斯"),
    ENTRATI("EntratiSyndicate", "entrati", "英择谛");

    private final String code;
    private final String sectionKey;
    private final String title;

    WarframeOpenWorldSyndicate(String code, String sectionKey, String title) {
        this.code = code;
        this.sectionKey = sectionKey;
        this.title = title;
    }

    /** @param value 当前官方赏金的集团代码；@return 是否属于此平原。 */
    public boolean matches(String value) {
        return code.equals(value);
    }

    /** @return 官方动态数据中的集团代码。 */
    public String code() {
        return code;
    }

    /** @return 当前平原对应的管理端分区编码。 */
    public String sectionKey() {
        return sectionKey;
    }

    /** @return 当前平原中文标题。 */
    public String title() {
        return title;
    }
}
