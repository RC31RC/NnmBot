package com.nnm.bot.warframe.constant;

/** 社区数据刷新时，一条记录相对于当前数据库状态的变化类型。 */
public enum WarframeCommunityRecordChangeType {
    ADDED,
    CHANGED,
    REMOVED,
    UNCHANGED,
    SELF_MAINTAINED;

    public boolean isOperation() {
        return this == ADDED || this == CHANGED || this == REMOVED;
    }
}
