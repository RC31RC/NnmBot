package com.nnm.bot.core.channel.runtime;

import cn.hutool.core.text.CharSequenceUtil;

import org.springframework.beans.factory.ObjectProvider;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.constant.BotChannelCapability;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.channel.port.BotChannelConnector;
import com.nnm.bot.core.channel.constant.BotChannelErrorCode;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/** 统一管理数据库渠道实例的运行期启动、停止和配置重载。 */
@Component
public class BotChannelRuntimeManager {
    private final ReentrantLock lifecycleLock = new ReentrantLock();

    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private List<BotChannelConnector> connectors;
    @Resource
    private ObjectProvider<ClusterBotRuntime> cluster;

    public void start(String instanceId) {
        BotChannelInstanceRecord record = this.find(instanceId);
        if (!record.enabled()) {
            throw new BusinessException(BotChannelErrorCode.INSTANCE_DISABLED);
        }
        cluster.getObject().request(instanceId, true);
    }

    void startLocal(String instanceId) {
        lifecycleLock.lock();
        try {
            startLocal(instanceId, null);
        } finally {
            lifecycleLock.unlock();
        }
    }

    void startLocal(String instanceId, BotChannelKey authorizedKey) {
        lifecycleLock.lock();
        try {
            // 获得租约后仍重新读取数据库，缓存中的启用状态不能作为最终启动授权。
            BotChannelInstanceRecord record = instanceProvider.findForStart(instanceId)
                .orElseThrow(() -> new BusinessException(BotChannelErrorCode.INSTANCE_NOT_FOUND));
            if (!record.enabled()) {
                throw new BusinessException(BotChannelErrorCode.INSTANCE_DISABLED);
            }

            BotChannelKey key = new BotChannelKey(record.platform(), record.botAccountId());
            if (authorizedKey != null) {
                BusinessAssert.isTrue(authorizedKey.equals(key), BotChannelErrorCode.INSTANCE_TOPOLOGY_CHANGED);
                ClusterBotRuntime coordinator = cluster.getObject();
                BusinessAssert.isTrue(coordinator.isLocalOwner(key), BotChannelErrorCode.LOCAL_OWNER_LEASE_EXPIRED);
            }
            this.removeRuntime(record.instanceId());
            this.registry.register(record.instanceId(), key, this.capabilities(record.platform()), true);
            try {
                connector(record.platform()).startInstance(record);
            } catch (RuntimeException exception) {
                // 连接器可能已经创建了部分异步资源，启动失败时先统一释放，避免
                // 页面显示失败但后台仍保留可重连连接。
                try {
                    connector(record.platform()).stopInstance(record.instanceId());
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
                this.registry.startFailed(key, exception);
                throw new BusinessException(BotChannelErrorCode.INSTANCE_START_FAILED, exception);
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stop(String instanceId) {
        BotChannelInstanceRecord record = this.find(instanceId);
        this.registry.stopping(new BotChannelKey(record.platform(), record.botAccountId()));
        cluster.getObject().request(instanceId, false);
    }

    void stopLocal(String instanceId) {
        lifecycleLock.lock();
        try {
            this.connectors.forEach(connector -> connector.stopInstance(instanceId));
            this.registry.stopped(instanceId);
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void remove(String instanceId) {
        cluster.getObject().request(instanceId, false);
    }

    void removeRuntime(String instanceId) {
        lifecycleLock.lock();
        try {
            this.connectors.forEach(connector -> connector.stopInstance(instanceId));
            this.registry.unregister(instanceId);
        } finally {
            lifecycleLock.unlock();
        }
    }

    private BotChannelInstanceRecord find(String instanceId) {
        if (CharSequenceUtil.isBlank(instanceId)) {
            throw new BusinessException(BotChannelErrorCode.INSTANCE_ID_REQUIRED);
        }
        String normalizedInstanceId = CharSequenceUtil.trim(instanceId);
        return this.instanceProvider.findAll().stream()
            .filter(record -> record.instanceId().equals(normalizedInstanceId)).findFirst()
            .orElseThrow(() -> new BusinessException(BotChannelErrorCode.INSTANCE_NOT_FOUND));
    }

    private Set<BotChannelCapability> capabilities(BotPlatform platform) {
        return platform.capabilities();
    }

    private BotChannelConnector connector(BotPlatform platform) {
        List<BotChannelConnector> matches =
            connectors.stream().filter(connector -> connector.platform() == platform).toList();
        if (matches.size() != 1) {
            throw new BusinessException(BotChannelErrorCode.CONNECTOR_REGISTRATION_INVALID);
        }
        return matches.getFirst();
    }
}
