package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import java.util.UUID;
import java.util.function.Consumer;

/** 对通知任务状态执行受控的原子更新。 */
public interface BotNotificationStateService {

    /** 在任务锁保护下修改并持久化任务。
     * @param id 任务标识
     * @param change 对任务执行的状态变更
     * @return 更新后的任务实体
     */
    BotNotificationJob update(UUID id, Consumer<BotNotificationJob> change);
}
