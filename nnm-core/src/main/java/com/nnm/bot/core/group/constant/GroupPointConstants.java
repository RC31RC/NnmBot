package com.nnm.bot.core.group.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 群组积分的稳定记账类型与互斥用途，枚举名保持数据库和协议既有值。 */
public final class GroupPointConstants {
    private GroupPointConstants() {}

    /** 积分流水的业务原因，不以描述文本判断是否可以退款。 */
    public enum TransactionType {
        SIGN_IN, COMMAND_COST, COMMAND_REWARD, REFUND, ADMIN_ADJUST
    }

    /** 积分账户与账务状态的稳定错误码；账本矛盾属于服务故障，不报成用户输入错误。 */
    @Getter
    @RequiredArgsConstructor
    public enum PointErrorCode implements ErrorCode {
        ACCOUNT_NOT_FOUND(40410, "积分账户不存在", HttpStatus.NOT_FOUND),
        ACCOUNT_UNAVAILABLE(40910, "积分账户当前不可用", HttpStatus.CONFLICT),
        GROUP_UNAVAILABLE(40911, "逻辑群组已不可用", HttpStatus.CONFLICT),
        LEDGER_INCONSISTENT(50073, "积分账务状态不一致，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR),
        ACCOUNT_ID_INVALID(400701, "积分账户标识必须为正数", HttpStatus.BAD_REQUEST),
        TRANSACTION_TYPE_REQUIRED(400702, "积分流水类型不能为空", HttpStatus.BAD_REQUEST),
        AMOUNT_INVALID(400703, "积分变更数量必须为正数，余额对齐允许为零", HttpStatus.BAD_REQUEST),
        BALANCE_INVALID(400704, "积分余额不能为负数", HttpStatus.BAD_REQUEST),
        IDEMPOTENCY_KEY_REQUIRED(400705, "idempotencyKey不能为空", HttpStatus.BAD_REQUEST),
        GROUP_UNAVAILABLE_AFTER_COMMAND(40912, "命令执行完成，但逻辑群组已不可用", HttpStatus.CONFLICT),
        MEMBER_UNAVAILABLE_AFTER_COMMAND(40913, "命令执行完成，但群组成员已不存在", HttpStatus.CONFLICT);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }

}
