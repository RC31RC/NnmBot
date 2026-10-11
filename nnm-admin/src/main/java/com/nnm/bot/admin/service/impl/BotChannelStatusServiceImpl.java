package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.service.BotChannelStatusService;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotChannelStatus;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class BotChannelStatusServiceImpl implements BotChannelStatusService {
    @Resource
    private BotChannelInstanceRepository repository;
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;

    @Override
    public boolean isDistributed() {
        return clusterProvider.getIfAvailable() != null;
    }

    @Override
    public List<BotChannelSnapshot> snapshots() {
        ClusterBotRuntime cluster = clusterProvider.getIfAvailable();
        if (cluster == null) return registry.snapshots();
        return repository.findAllByOrderByPlatformAscInstanceIdAsc().stream().map(instance -> {
            BotChannelKey key = new BotChannelKey(instance.getPlatform(), instance.getBotAccountId());
            return cluster.snapshot(key).orElseGet(() -> new BotChannelSnapshot(instance.getInstanceId(),
                    key, instance.getPlatform().capabilities(),
                    instance.isEnabled() ? BotChannelStatus.STOPPED : BotChannelStatus.DISABLED, null, null));
        }).toList();
    }
}
