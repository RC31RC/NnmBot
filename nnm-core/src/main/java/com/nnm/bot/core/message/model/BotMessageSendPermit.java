package com.nnm.bot.core.message.model;

/** 预留在创建请求前转为已使用；只有协议明确未接受时才归还。 */
public interface BotMessageSendPermit extends AutoCloseable {
    Integer messageSequence();

    void beforeSend();

    void rejected();

    @Override
    void close();

    static BotMessageSendPermit unrestricted() {
        return new BotMessageSendPermit() {
            @Override
            public Integer messageSequence() { return null; }

            @Override
            public void beforeSend() { }

            @Override
            public void rejected() { }

            @Override
            public void close() { }
        };
    }
}
