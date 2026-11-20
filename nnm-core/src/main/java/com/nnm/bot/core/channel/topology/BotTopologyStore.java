package com.nnm.bot.core.channel.topology;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

/** 缓存完整的渠道拓扑快照，读取期间保留上一份有效快照。 */
@Component
public class BotTopologyStore {
    private final ReentrantLock refreshLock = new ReentrantLock();

    /** 从管理模块加载持久化渠道与作用域配置。 */
    @Resource
    private BotTopologyLoader loader;
    /** 多线程读取的不可变拓扑快照。 */
    private volatile BotTopologySnapshot snapshot;

    /** 启动时加载首份拓扑，构建失败则终止启动。 */
    @PostConstruct
    public void refresh() {
        refreshLock.lock();
        try {
            // 先完整构建再原子发布；数据库或解密失败会抛出，读者继续使用上一份完整快照。
            BotTopologySnapshot loaded = loader.load();
            if (snapshot == null || !snapshot.version().equals(loaded.version()))
                snapshot = loaded;
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * 仅在收到版本变化时重载拓扑。
     *
     * @param version 广播的目标版本
     */
    public void refreshIfChanged(String version) {
        refreshLock.lock();
        try {
            if (snapshot == null || !snapshot.version().equals(version))
                refresh();
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * 返回当前完整拓扑快照。
     *
     * @return 已发布的拓扑快照
     */
    public BotTopologySnapshot snapshot() {
        return snapshot;
    }
}
