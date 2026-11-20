package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;

import java.util.List;
import java.util.Optional;

/** 为核心连接器提供持久化渠道实例；机器人配置不存在时所有渠道保持关闭。 */
public interface BotChannelInstanceProvider {

    /**
     * 返回当前配置的渠道实例快照。
     *
     * @return 所有渠道实例
     */
    List<BotChannelInstanceRecord> findAll();

    /**
     * 启动授权边界读取权威配置；适配器应绕过拓扑缓存。
     *
     * @param instanceId 持久化渠道实例标识
     * @return 匹配实例；不存在时为空
     */
    default Optional<BotChannelInstanceRecord> findForStart(String instanceId) {
        return findAll().stream().filter(record -> record.instanceId().equals(instanceId)).findFirst();
    }
}
