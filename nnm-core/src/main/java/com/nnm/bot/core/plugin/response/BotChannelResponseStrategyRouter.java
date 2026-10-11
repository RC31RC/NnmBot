package com.nnm.bot.core.plugin.response;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.api.BotChannelResponseStrategy;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用渠道编码索引输出策略，避免响应分发器依赖具体平台协议。
 */
@Component
public class BotChannelResponseStrategyRouter {

    @Resource
    private List<BotChannelResponseStrategy> strategyList;

    private Map<BotPlatform, BotChannelResponseStrategy> strategies = Map.of();

    @PostConstruct
    private void initialize() {
        Map<BotPlatform, BotChannelResponseStrategy> registry = new HashMap<>();
        for (BotChannelResponseStrategy strategy : this.strategyList) {
            BotChannelResponseStrategy previous = registry.put(strategy.platform(), strategy);
            if (ObjectUtil.isNotNull(previous)) {
                throw new IllegalStateException(
                        "Duplicate response strategy for platform " + strategy.platform());
            }
        }
        this.strategies = Map.copyOf(registry);
    }

    public void send(BotMessageContext context, ResponsePlan plan) {
        BotChannelResponseStrategy strategy = this.strategies.get(context.platform());
        if (ObjectUtil.isNull(strategy)) {
            throw new BusinessException(
                    BotChannelErrorCode.RESPONSE_STRATEGY_NOT_REGISTERED,
                    "未注册响应策略: " + context.platform());
        }
        strategy.send(context, plan);
    }
}
