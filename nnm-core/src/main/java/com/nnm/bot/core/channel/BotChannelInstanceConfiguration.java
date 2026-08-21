package com.nnm.bot.core.channel;

import com.nnm.bot.core.onebot.config.OneBotProperties;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import com.nnm.bot.core.message.BotPlatform;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/** 将数据库渠道实例加载到现有配置对象；数据库是机器人实例的唯一配置源。 */
@Component
public class BotChannelInstanceConfiguration {

    private final OneBotProperties oneBotProperties;
    private final QqOfficialProperties qqOfficialProperties;
    private final ObjectProvider<BotChannelInstanceProvider> provider;

    public BotChannelInstanceConfiguration(
            OneBotProperties oneBotProperties,
            QqOfficialProperties qqOfficialProperties,
            ObjectProvider<BotChannelInstanceProvider> provider) {
        this.oneBotProperties = oneBotProperties;
        this.qqOfficialProperties = qqOfficialProperties;
        this.provider = provider;
    }

    @PostConstruct
    void load() {
        // 清空配置属性中的默认值，避免 application YAML 意外驱动机器人连接。
        oneBotProperties.setEnabled(false);
        oneBotProperties.setInstances(List.of());
        qqOfficialProperties.setEnabled(false);
        qqOfficialProperties.setInstances(List.of());

        BotChannelInstanceProvider instanceProvider = provider.getIfAvailable();
        if (instanceProvider == null) {
            return;
        }
        List<BotChannelInstanceRecord> records = instanceProvider.findAll();
        if (records == null || records.isEmpty()) {
            return;
        }

        List<BotChannelInstanceRecord> oneBotRecords = records.stream()
                .filter(record -> record.platform() == BotPlatform.ONEBOT)
                .toList();
        oneBotProperties.setEnabled(oneBotRecords.stream()
                .anyMatch(BotChannelInstanceRecord::enabled));
        oneBotProperties.setInstances(oneBotRecords.stream()
                .map(this::toOneBotInstance)
                .toList());

        List<BotChannelInstanceRecord> qqRecords = records.stream()
                .filter(record -> record.platform() == BotPlatform.QQ_OFFICIAL)
                .toList();
        qqOfficialProperties.setEnabled(qqRecords.stream()
                .anyMatch(BotChannelInstanceRecord::enabled));
        qqOfficialProperties.setInstances(qqRecords.stream()
                .map(this::toQqOfficialInstance)
                .toList());
    }

    private OneBotProperties.Instance toOneBotInstance(BotChannelInstanceRecord record) {
        OneBotProperties.Instance instance = new OneBotProperties.Instance();
        instance.setId(record.instanceId());
        instance.setBotAccountId(record.botAccountId());
        instance.setEnabled(record.enabled());
        OneBotProperties.Connection connection = new OneBotProperties.Connection();
        connection.setUrl(record.connectionUrl());
        connection.setAccessToken(record.accessToken());
        instance.setConnection(connection);
        OneBotProperties.Runtime runtime = new OneBotProperties.Runtime();
        runtime.setReconnectInterval(positiveOrDefault(
                record.reconnectInterval(), Duration.ofSeconds(5)));
        instance.setRuntime(runtime);
        return instance;
    }

    private QqOfficialProperties.Instance toQqOfficialInstance(
            BotChannelInstanceRecord record) {
        QqOfficialProperties.Instance instance = new QqOfficialProperties.Instance();
        instance.setId(record.instanceId());
        instance.setEnabled(record.enabled());
        QqOfficialProperties.Connection connection = new QqOfficialProperties.Connection();
        connection.setAppId(record.appId());
        connection.setClientSecret(record.clientSecret());
        connection.setTokenUrl(record.tokenUrl());
        connection.setApiBaseUrl(record.apiBaseUrl());
        connection.setGatewayUrl(record.gatewayUrl());
        instance.setConnection(connection);
        QqOfficialProperties.Runtime runtime = new QqOfficialProperties.Runtime();
        runtime.setIntents(record.intents());
        runtime.setGatewayConnectTimeout(positiveOrDefault(
                record.gatewayConnectTimeout(), Duration.ofSeconds(10)));
        runtime.setReconnectDelay(nonNegativeOrDefault(
                record.reconnectDelay(), Duration.ofSeconds(5)));
        instance.setRuntime(runtime);
        return instance;
    }

    private static Duration positiveOrDefault(Duration value, Duration fallback) {
        return value == null || value.isZero() || value.isNegative() ? fallback : value;
    }

    private static Duration nonNegativeOrDefault(Duration value, Duration fallback) {
        return value == null || value.isNegative() ? fallback : value;
    }
}
