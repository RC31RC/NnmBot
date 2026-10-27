package com.nnm.bot.admin.service.bot.notification.impl;

import com.nnm.bot.admin.service.bot.notification.BotNotificationStateService;

import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 行锁只覆盖短数据库状态变更，不跨线程、渲染或平台发送持有事务。 */
@Service
public class BotNotificationStateServiceImpl implements BotNotificationStateService {
    @Resource
    private BotNotificationJobRepository jobs;

    @Transactional
    public BotNotificationJob update(UUID id, Consumer<BotNotificationJob> change) {
        BotNotificationJob job = jobs.lockById(id).orElseThrow(() -> new IllegalArgumentException("通知任务不存在"));
        change.accept(job);
        job.setUpdatedAt(Instant.now());
        return jobs.save(job);
    }
}
