package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 修改群组成员当前积分余额的请求。 */
public record BotGroupMemberPointsUpdateRequest(
        /** 修改后的积分余额。 */
        @NotNull
        @Min(0)
        Long points) {
}
