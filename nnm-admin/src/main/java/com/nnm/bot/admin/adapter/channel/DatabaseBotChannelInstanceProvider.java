package com.nnm.bot.admin.adapter.channel;

import cn.hutool.core.util.ObjUtil;

import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.channel.OneBotChannelConfig;
import com.nnm.bot.admin.pojo.entity.bot.channel.QqOfficialChannelConfig;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.channel.constant.BotPlatform;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Comparator;
import java.util.OptionalLong;
import java.util.Optional;

/** 从 bot_channel_instance 表向核心连接器提供配置。 */
@Component
public class DatabaseBotChannelInstanceProvider
    implements BotChannelInstanceProvider, BotChannelInstanceIdentityResolver {

    @Resource
    private BotTopologyStore topology;

    @Resource
    private BotChannelInstanceRepository repository;
    @Resource
    private BotCredentialCipher credentialCipher;

    @Override
    public Optional<BotChannelInstanceRecord> findForStart(String instanceId) {
        return repository.findByInstanceIdIgnoreCase(instanceId).map(instance -> toRecord(instance, credentialCipher));
    }

    @Override
    public List<BotChannelInstanceRecord> findAll() {
        return topology.snapshot().instances().values().stream()
            .sorted(Comparator.comparing(BotChannelInstanceRecord::instanceId)).toList();
    }

    @Override
    public OptionalLong resolveId(BotPlatform platform, String botAccountId) {
        Long id = topology.snapshot().instanceIds().get(new BotChannelKey(platform, botAccountId));
        return id == null ? OptionalLong.empty() : OptionalLong.of(id);
    }

    static BotChannelInstanceRecord toRecord(BotChannelInstance instance, BotCredentialCipher credentialCipher) {
        OneBotChannelConfig oneBot = instance.getOneBotConfig();
        QqOfficialChannelConfig official = instance.getQqOfficialConfig();
        return new BotChannelInstanceRecord(instance.getPlatform(), instance.getInstanceId(),
            instance.getBotAccountId(), instance.isEnabled(), ObjUtil.isNull(oneBot) ? null : oneBot.getConnectionUrl(),
            ObjUtil.isNull(oneBot) ? null : credentialCipher.decrypt(oneBot.getAccessToken()),
            ObjUtil.isNull(official) ? null : official.getAppId(),
            ObjUtil.isNull(official) ? null : credentialCipher.decrypt(official.getClientSecret()),
            ObjUtil.isNull(official) ? null : official.getTokenUrl(),
            ObjUtil.isNull(official) ? null : official.getApiBaseUrl(),
            ObjUtil.isNull(official) ? null : official.getGatewayUrl(),
            seconds(ObjUtil.isNull(oneBot) ? null : oneBot.getReconnectIntervalSeconds()),
            ObjUtil.isNull(official) ? 0L : official.getIntents(),
            seconds(ObjUtil.isNull(official) ? null : official.getGatewayConnectTimeoutSeconds()),
            seconds(ObjUtil.isNull(official) ? null : official.getReconnectDelaySeconds()));
    }

    private static Duration seconds(Long value) {
        return ObjUtil.isNull(value) ? null : Duration.ofSeconds(value);
    }
}
