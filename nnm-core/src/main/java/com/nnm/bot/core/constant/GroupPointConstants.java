package com.nnm.bot.core.constant;

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
}
