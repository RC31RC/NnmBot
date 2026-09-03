package com.nnm.bot.core.channel.runtime;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.onebot.connection.OneBotWebSocketClient;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Set;

/** 统一管理数据库渠道实例的运行期启动、停止和配置重载。 */
@Component
public class BotChannelRuntimeManager {

    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private OneBotWebSocketClient oneBotClient;
    @Resource
    private QqOfficialGatewayClient qqOfficialClient;

    public synchronized void start(String instanceId) {
        BotChannelInstanceRecord record = this.find(instanceId);
        if (!record.enabled()) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_DISABLED,
                    "机器人实例已禁用: " + record.instanceId());
        }

        this.removeRuntime(record.instanceId());
        BotChannelKey key = new BotChannelKey(record.platform(), record.botAccountId());
        this.registry.register(
                record.instanceId(), key, this.capabilities(record.platform()), true);
        try {
            switch (record.platform()) {
                case ONEBOT -> this.oneBotClient.startInstance(record);
                case QQ_OFFICIAL -> this.qqOfficialClient.startInstance(record);
            }
        } catch (RuntimeException exception) {
            this.registry.failed(key, exception);
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_START_FAILED,
                    "启动机器人实例失败: " + record.instanceId(),
                    exception);
        }
    }

    public synchronized void stop(String instanceId) {
        this.oneBotClient.stopInstance(instanceId);
        this.qqOfficialClient.stopInstance(instanceId);
        this.registry.stopped(instanceId);
    }

    public synchronized void remove(String instanceId) {
        this.removeRuntime(instanceId);
        this.registry.unregister(instanceId);
    }

    private void removeRuntime(String instanceId) {
        this.oneBotClient.stopInstance(instanceId);
        this.qqOfficialClient.stopInstance(instanceId);
        this.registry.unregister(instanceId);
    }

    private BotChannelInstanceRecord find(String instanceId) {
        if (StrUtil.isBlank(instanceId)) {
            throw new BusinessException(
                    CommonErrorCode.PARAM_INVALID,
                    "机器人实例标识不能为空");
        }
        String normalizedInstanceId = instanceId.trim();
        return this.instanceProvider.findAll().stream()
                .filter(record -> record.instanceId().equals(normalizedInstanceId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        BotChannelErrorCode.INSTANCE_NOT_FOUND,
                        "机器人实例不存在: " + normalizedInstanceId));
    }

    private Set<BotChannelCapability> capabilities(BotPlatform platform) {
        return platform.capabilities();
    }
}
