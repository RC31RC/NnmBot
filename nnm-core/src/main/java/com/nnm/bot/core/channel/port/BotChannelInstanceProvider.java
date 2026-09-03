package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;

import java.util.List;

/** 为核心连接器提供持久化渠道实例；机器人配置不存在时所有渠道保持关闭。 */
public interface BotChannelInstanceProvider {

    List<BotChannelInstanceRecord> findAll();
}
