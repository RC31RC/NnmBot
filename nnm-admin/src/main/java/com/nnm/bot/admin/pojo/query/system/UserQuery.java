package com.nnm.bot.admin.pojo.query.system;

/** 用户列表查询参数。 */
public record UserQuery(
        String userName,
        Integer current,
        Integer size
) {
}
