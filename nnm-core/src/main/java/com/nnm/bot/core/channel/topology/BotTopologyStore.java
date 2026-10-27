package com.nnm.bot.core.channel.topology;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class BotTopologyStore {
    private final ReentrantLock refreshLock = new ReentrantLock();

    @Resource
    private BotTopologyLoader loader;
    private volatile BotTopologySnapshot snapshot;

    @PostConstruct
    public void refresh() {
        refreshLock.lock();
        try {
            // 先完整构建再原子发布；数据库或解密失败会抛出，读者继续使用上一份完整快照。
            BotTopologySnapshot loaded = loader.load();
            if (snapshot == null || !snapshot.version().equals(loaded.version())) snapshot = loaded;
        } finally {
            refreshLock.unlock();
        }
    }

    public void refreshIfChanged(String version) {
        refreshLock.lock();
        try {
            if (snapshot == null || !snapshot.version().equals(version)) refresh();
        } finally {
            refreshLock.unlock();
        }
    }

    public BotTopologySnapshot snapshot() {
        return snapshot;
    }
}
