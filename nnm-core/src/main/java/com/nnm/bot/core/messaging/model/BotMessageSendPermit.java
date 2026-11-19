package com.nnm.bot.core.messaging.model;

/** 预留在创建请求前转为已使用；只有协议明确未接受时才归还。 */
public interface BotMessageSendPermit extends AutoCloseable {

    /** 返回协议分配的消息序号；不需要额度的平台返回空值。 */
    Integer messageSequence();

    /** 在创建平台消息请求前将预留额度标记为已使用。 */
    void beforeSend();

    /** 平台明确拒绝创建消息时归还预留额度。 */
    void rejected();

    /** 释放尚未使用的预留额度；重复关闭应无副作用。 */
    @Override
    void close();

    /** 创建不执行额度管理的生命周期句柄。
     *
     * @return 所有操作均为空操作的许可
     */
    static BotMessageSendPermit unrestricted() {
        return new BotMessageSendPermit() {
            @Override
            public Integer messageSequence() {
                return null;
            }

            @Override
            public void beforeSend() {}

            @Override
            public void rejected() {}

            @Override
            public void close() {}
        };
    }
}
