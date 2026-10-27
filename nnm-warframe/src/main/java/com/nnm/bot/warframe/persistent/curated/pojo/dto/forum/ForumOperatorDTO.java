package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

/**
 * 当前论坛管理操作人及其稳定权限。
 */
public record ForumOperatorDTO(
        long userId,
        int authorityLevel,
        boolean canUpdate,
        boolean canDelete
) {

    /**
     * 判断是否满足可以更新条件。
     *
     * @param ownerUserId 所属人用户标识。
     * @param ownerAuthorityLevel 所属人权限等级。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean canUpdate(long ownerUserId, int ownerAuthorityLevel) {
        return canUpdate && canManage(ownerUserId, ownerAuthorityLevel);
    }

    /**
     * 判断是否满足可以删除条件。
     *
     * @param ownerUserId 所属人用户标识。
     * @param ownerAuthorityLevel 所属人权限等级。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean canDelete(long ownerUserId, int ownerAuthorityLevel) {
        return canDelete && canManage(ownerUserId, ownerAuthorityLevel);
    }

    /**
     * 判断是否满足可以管理条件。
     *
     * @param ownerUserId 所属人用户标识。
     * @param ownerAuthorityLevel 所属人权限等级。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean canManage(long ownerUserId, int ownerAuthorityLevel) {
        return userId == ownerUserId || authorityLevel > ownerAuthorityLevel;
    }
}
