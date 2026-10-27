package com.nnm.bot.admin.adapter.channel;

import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.topology.BotTopologyLoader;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Component
public class DatabaseBotTopologyLoader implements BotTopologyLoader {
    @Resource
    private BotChannelInstanceRepository instances;
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotGroupBotRepository bindings;
    @Resource
    private BotCredentialCipher cipher;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
    public BotTopologySnapshot load() {
        // 连接配置、群组和绑定来自同一数据库快照，避免刷新时拼出半新半旧的拓扑。
        Map<Long, BotChannelInstanceRecord> records = instances.findAllByOrderByPlatformAscInstanceIdAsc().stream()
                .collect(Collectors.toMap(instance -> instance.getId(),
                        instance -> DatabaseBotChannelInstanceProvider.toRecord(instance, cipher)));
        return BotTopologySnapshot.of(records, groups.findAll(), bindings.findAll());
    }
}
