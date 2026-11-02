package com.nnm.bot.core.constant;

/** 管理任务的稳定互斥用途；变更键值需要同步评估全部调用方。 */
public enum AdminTaskLockPurpose {
    SUBMIT("submit"), WORK("work");

    private final String key;

    AdminTaskLockPurpose(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
