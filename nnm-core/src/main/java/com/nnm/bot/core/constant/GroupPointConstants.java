package com.nnm.bot.core.constant;

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

    /** 积分账务的操作互斥，与成员实体资源锁区分。 */
    public enum LockPurpose {
        REFUND("refund");

        private final String key;

        LockPurpose(String key) {
            this.key = key;
        }

        /** 返回用于分布式互斥的稳定操作名。 */
        public String key() {
            return key;
        }
    }

    /** 积分账户与账务状态的稳定错误码；账本矛盾属于服务故障，不报成用户输入错误。 */
    @Getter
    @RequiredArgsConstructor
    public enum PointErrorCode implements ErrorCode {
        ACCOUNT_NOT_FOUND(40410, "积分账户不存在", HttpStatus.NOT_FOUND),
        ACCOUNT_UNAVAILABLE(40910, "积分账户当前不可用", HttpStatus.CONFLICT),
        GROUP_UNAVAILABLE(40911, "逻辑群组已不可用", HttpStatus.CONFLICT),
        LEDGER_INCONSISTENT(50010, "积分账务状态不一致，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }

}
