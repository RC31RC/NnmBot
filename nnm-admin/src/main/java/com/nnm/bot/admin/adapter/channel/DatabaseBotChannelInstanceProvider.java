package com.nnm.bot.admin.adapter.channel;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.admin.domain.BotChannelInstance;
import com.nnm.bot.admin.domain.OneBotChannelConfig;
import com.nnm.bot.admin.domain.QqOfficialChannelConfig;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.constant.BotPlatform;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.OptionalLong;

/** 从 bot_channel_instance 表向核心连接器提供配置。 */
@Component
public class DatabaseBotChannelInstanceProvider
        implements BotChannelInstanceProvider, BotChannelInstanceIdentityResolver {

    @Resource
    private BotChannelInstanceRepository repository;
    @Resource
    private BotCredentialCipher credentialCipher;

    @Override
    public List<BotChannelInstanceRecord> findAll() {
        return repository.findAllByOrderByPlatformAscInstanceIdAsc().stream()
                .map(this::toRecord)
                .toList();
    }

    @Override
    public OptionalLong resolveId(BotPlatform platform, String botAccountId) {
        return repository.findByPlatformAndBotAccountId(platform, botAccountId)
                .map(BotChannelInstance::getId)
                .map(OptionalLong::of)
                .orElseGet(OptionalLong::empty);
    }

    private BotChannelInstanceRecord toRecord(BotChannelInstance instance) {
        OneBotChannelConfig oneBot = instance.getOneBotConfig();
        QqOfficialChannelConfig official = instance.getQqOfficialConfig();
        return new BotChannelInstanceRecord(
                instance.getPlatform(),
                instance.getInstanceId(),
                instance.getBotAccountId(),
                instance.isEnabled(),
                ObjectUtil.isNull(oneBot) ? null : oneBot.getConnectionUrl(),
                ObjectUtil.isNull(oneBot) ? null : credentialCipher.decrypt(oneBot.getAccessToken()),
                ObjectUtil.isNull(official) ? null : official.getAppId(),
                ObjectUtil.isNull(official) ? null : credentialCipher.decrypt(official.getClientSecret()),
                ObjectUtil.isNull(official) ? null : official.getTokenUrl(),
                ObjectUtil.isNull(official) ? null : official.getApiBaseUrl(),
                ObjectUtil.isNull(official) ? null : official.getGatewayUrl(),
                seconds(ObjectUtil.isNull(oneBot) ? null : oneBot.getReconnectIntervalSeconds()),
                ObjectUtil.isNull(official) ? 0L : official.getIntents(),
                seconds(ObjectUtil.isNull(official) ? null : official.getGatewayConnectTimeoutSeconds()),
                seconds(ObjectUtil.isNull(official) ? null : official.getReconnectDelaySeconds()));
    }

    private static Duration seconds(Long value) {
        return ObjectUtil.isNull(value) ? null : Duration.ofSeconds(value);
    }
}
