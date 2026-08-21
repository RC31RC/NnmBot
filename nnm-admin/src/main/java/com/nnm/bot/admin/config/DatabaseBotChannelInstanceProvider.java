package com.nnm.bot.admin.config;

import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.admin.domain.BotChannelInstance;
import com.nnm.bot.admin.domain.OneBotChannelConfig;
import com.nnm.bot.admin.domain.QqOfficialChannelConfig;
import com.nnm.bot.core.channel.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.BotChannelInstanceRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/** 从 bot_channel_instance 表向核心连接器提供配置。 */
@Component
public class DatabaseBotChannelInstanceProvider implements BotChannelInstanceProvider {

    private final BotChannelInstanceRepository repository;
    private final BotCredentialCipher credentialCipher;

    public DatabaseBotChannelInstanceProvider(
            BotChannelInstanceRepository repository,
            BotCredentialCipher credentialCipher) {
        this.repository = repository;
        this.credentialCipher = credentialCipher;
    }

    @Override
    public List<BotChannelInstanceRecord> findAll() {
        return repository.findAllByOrderByPlatformAscInstanceIdAsc().stream()
                .map(this::toRecord)
                .toList();
    }

    private BotChannelInstanceRecord toRecord(BotChannelInstance instance) {
        OneBotChannelConfig oneBot = instance.getOneBotConfig();
        QqOfficialChannelConfig official = instance.getQqOfficialConfig();
        return new BotChannelInstanceRecord(
                instance.getPlatform(),
                instance.getInstanceId(),
                instance.getBotAccountId(),
                instance.isEnabled(),
                oneBot == null ? null : oneBot.getConnectionUrl(),
                oneBot == null ? null : credentialCipher.decrypt(oneBot.getAccessToken()),
                official == null ? null : official.getAppId(),
                official == null ? null : credentialCipher.decrypt(official.getClientSecret()),
                official == null ? null : official.getTokenUrl(),
                official == null ? null : official.getApiBaseUrl(),
                official == null ? null : official.getGatewayUrl(),
                seconds(oneBot == null ? null : oneBot.getReconnectIntervalSeconds()),
                official == null ? 0L : official.getIntents(),
                seconds(official == null ? null : official.getGatewayConnectTimeoutSeconds()),
                seconds(official == null ? null : official.getReconnectDelaySeconds()));
    }

    private static Duration seconds(Long value) {
        return value == null ? null : Duration.ofSeconds(value);
    }
}
