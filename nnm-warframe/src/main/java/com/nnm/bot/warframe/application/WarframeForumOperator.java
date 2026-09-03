package com.nnm.bot.warframe.application;

/**
 * 当前论坛管理操作人及其稳定权限。
 */
public record WarframeForumOperator(
        long userId,
        int authorityLevel,
        boolean canUpdate,
        boolean canDelete
) {

    public boolean canUpdate(long ownerUserId, int ownerAuthorityLevel) {
        return canUpdate && canManage(ownerUserId, ownerAuthorityLevel);
    }

    public boolean canDelete(long ownerUserId, int ownerAuthorityLevel) {
        return canDelete && canManage(ownerUserId, ownerAuthorityLevel);
    }

    private boolean canManage(long ownerUserId, int ownerAuthorityLevel) {
        return userId == ownerUserId || authorityLevel > ownerAuthorityLevel;
    }
}
