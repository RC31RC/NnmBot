package com.nnm.bot.admin.constant;

/** 机器人能力角色分配使用的操作互斥，不与后台用户角色资源混用。 */
public enum BotRobotRoleLockPurpose {
    INSTANCE_ROLES("instance-roles");

    private final String key;

    BotRobotRoleLockPurpose(String key) {
        this.key = key;
    }

    /** 返回单实例角色分配的稳定操作名。 */
    public String key() {
        return key;
    }
}
