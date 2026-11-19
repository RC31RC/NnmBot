package com.nnm.bot.core.channel.registry;

import com.nnm.bot.core.channel.port.BotGroupMemberQueryProvider;
import com.nnm.bot.core.channel.port.BotLocalScopeDiscoveryProvider;
import com.nnm.bot.core.channel.port.BotMessageReadProvider;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.channel.constant.BotPlatform;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/** 按平台索引渠道扩展点，并在启动时拒绝同类能力重复注册。 */
@Component
public class ChannelProviderRegistry {

    @Resource
    private List<BotScopeDiscoveryProvider> scopeDiscoveryProviders = List.of();
    @Resource
    private List<BotLocalScopeDiscoveryProvider> localScopeDiscoveryProviders = List.of();
    @Resource
    private List<BotGroupMemberQueryProvider> groupMemberQueryProviders = List.of();
    @Resource
    private List<BotMessageReadProvider> messageReadProviders = List.of();

    private Map<BotPlatform, BotScopeDiscoveryProvider> scopeDiscoveryByPlatform = Map.of();
    private Map<BotPlatform, BotLocalScopeDiscoveryProvider> localScopeDiscoveryByPlatform = Map.of();
    private Map<BotPlatform, BotGroupMemberQueryProvider> groupMemberQueryByPlatform = Map.of();
    private Map<BotPlatform, BotMessageReadProvider> messageReadByPlatform = Map.of();

    /** 在所有 Provider 注入后构建不可变索引；配置冲突在应用启动时失败。 */
    @PostConstruct
    void initialize() {
        scopeDiscoveryByPlatform = index(scopeDiscoveryProviders, BotScopeDiscoveryProvider::platform, "作用域发现");
        localScopeDiscoveryByPlatform =
            index(localScopeDiscoveryProviders, BotLocalScopeDiscoveryProvider::platform, "本地作用域发现");
        groupMemberQueryByPlatform = index(groupMemberQueryProviders, BotGroupMemberQueryProvider::platform, "群成员查询");
        messageReadByPlatform = index(messageReadProviders, BotMessageReadProvider::platform, "消息读取");
    }

    /** 返回平台的作用域发现 Provider。 */
    public Optional<BotScopeDiscoveryProvider> scopeDiscovery(BotPlatform platform) {
        return Optional.ofNullable(scopeDiscoveryByPlatform.get(platform));
    }

    /** 返回平台的本地作用域发现 Provider。 */
    public Optional<BotLocalScopeDiscoveryProvider> localScopeDiscovery(BotPlatform platform) {
        return Optional.ofNullable(localScopeDiscoveryByPlatform.get(platform));
    }

    /** 返回平台的群成员查询 Provider。 */
    public Optional<BotGroupMemberQueryProvider> groupMemberQuery(BotPlatform platform) {
        return Optional.ofNullable(groupMemberQueryByPlatform.get(platform));
    }

    /** 返回平台的消息读取 Provider。 */
    public Optional<BotMessageReadProvider> messageRead(BotPlatform platform) {
        return Optional.ofNullable(messageReadByPlatform.get(platform));
    }

    private static <T> Map<BotPlatform, T> index(List<T> providers, Function<T, BotPlatform> platformOf,
        String capability) {
        Map<BotPlatform, T> indexed = new EnumMap<>(BotPlatform.class);
        for (T provider : providers) {
            BotPlatform platform = platformOf.apply(provider);
            if (platform == null) {
                throw new IllegalStateException(capability + " Provider 未声明平台: " + provider.getClass().getName());
            }
            T previous = indexed.putIfAbsent(platform, provider);
            if (previous != null) {
                throw new IllegalStateException(capability + " Provider 平台重复: " + platform + " ("
                    + previous.getClass().getName() + ", " + provider.getClass().getName() + ")");
            }
        }
        return Map.copyOf(indexed);
    }
}
