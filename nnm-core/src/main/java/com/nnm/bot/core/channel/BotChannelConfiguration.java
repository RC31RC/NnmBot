package com.nnm.bot.core.channel;

import com.nnm.bot.core.message.BotPlatform;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.Set;

/** 将各协议配置注册为统一的渠道实例。 */
@Configuration(proxyBeanMethods = false)
public class BotChannelConfiguration {

    public static final Set<BotChannelCapability> ONEBOT_CAPABILITIES =
            Set.copyOf(EnumSet.of(
                    BotChannelCapability.TEXT_MESSAGE,
                    BotChannelCapability.IMAGE_MESSAGE,
                    BotChannelCapability.GROUP_MEMBER_LIST,
                    BotChannelCapability.GROUP_MEMBER_DETAIL));

    private static final Set<BotChannelCapability> QQ_OFFICIAL_CAPABILITIES =
            Set.copyOf(EnumSet.of(
                    BotChannelCapability.TEXT_MESSAGE,
                    BotChannelCapability.IMAGE_MESSAGE,
                    BotChannelCapability.MARKDOWN_MESSAGE));

    private final BotChannelRegistry registry;
    private final OneBotProperties oneBotProperties;
    private final QqOfficialProperties qqOfficialProperties;
    private final BotChannelInstanceConfiguration instanceConfiguration;

    public BotChannelConfiguration(BotChannelRegistry registry,
                                   OneBotProperties oneBotProperties,
                                   QqOfficialProperties qqOfficialProperties,
                                   BotChannelInstanceConfiguration instanceConfiguration) {
        this.registry = registry;
        this.oneBotProperties = oneBotProperties;
        this.qqOfficialProperties = qqOfficialProperties;
        this.instanceConfiguration = instanceConfiguration;
    }

    @PostConstruct
    void registerChannels() {
        for (OneBotProperties.Instance instance : oneBotProperties.getInstances()) {
            for (String accountId : instance.configuredAccountIds()) {
                registry.register(
                        instance.channelInstanceId(accountId),
                        new BotChannelKey(BotPlatform.ONEBOT, accountId),
                        ONEBOT_CAPABILITIES,
                        oneBotProperties.isEnabled() && instance.isEnabled());
            }
        }
        for (QqOfficialProperties.Instance instance : qqOfficialProperties.getInstances()) {
            if (!StringUtils.hasText(instance.botAccountId())) {
                continue;
            }
            registry.register(
                    instance.getId(),
                    new BotChannelKey(BotPlatform.QQ_OFFICIAL, instance.botAccountId()),
                    QQ_OFFICIAL_CAPABILITIES,
                    qqOfficialProperties.isEnabled() && instance.isEnabled());
        }
    }
}
