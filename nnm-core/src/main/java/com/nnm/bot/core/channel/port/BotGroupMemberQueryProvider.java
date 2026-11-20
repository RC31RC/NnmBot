package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.constant.BotPlatform;

/** 查询已知平台群组或其他支持成员列表的作用域。 */
public interface BotGroupMemberQueryProvider {

    /** 返回此成员查询实现所属的平台。 */
    BotPlatform platform();

    /**
     * 查询平台作用域的成员列表。
     *
     * @param instance 执行查询的渠道实例
     * @param scopeKey 平台群组或作用域标识
     * @return 成员快照及完整性标记
     */
    BotMemberQueryResult query(BotChannelInstanceRecord instance, String scopeKey);
}
