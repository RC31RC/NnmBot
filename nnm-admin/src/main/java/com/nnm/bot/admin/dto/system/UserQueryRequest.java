package com.nnm.bot.admin.dto.system;

/** 用户列表查询参数。 */
public record UserQueryRequest(
        String userName,
        Integer current,
        Integer size
) {
}
