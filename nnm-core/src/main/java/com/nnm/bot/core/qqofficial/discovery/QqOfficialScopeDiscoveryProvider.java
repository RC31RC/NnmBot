package com.nnm.bot.core.qqofficial.discovery;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotPlatform;
import org.springframework.stereotype.Component;

/**
 * QQ 官方机器人目前没有公开的全量群组枚举能力，不能伪造发现结果。
 */
@Component
public class QqOfficialScopeDiscoveryProvider implements BotScopeDiscoveryProvider {

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        return BotScopeDiscoveryResult.unsupported(
                "QQ 官方公开 API 不能枚举机器人加入的全部群组，请手工填写已知群 OpenID");
    }
}
