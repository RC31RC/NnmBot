package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.constant.CommonErrorCode;

/** 正常业务拒绝也必须回滚；回复载荷在事务结束后由命令入口处理。 */
public final class CommandRejectedException extends BusinessException {

    private final CommandResult result;

    public CommandRejectedException(CommandResult result) {
        super(CommonErrorCode.UNPROCESSABLE_ENTITY, "命令未完成");
        this.result = result;
    }

    public CommandResult result() {
        return result;
    }
}
