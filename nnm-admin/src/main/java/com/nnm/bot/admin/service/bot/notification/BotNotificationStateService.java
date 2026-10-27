package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import java.util.UUID;
import java.util.function.Consumer;

public interface BotNotificationStateService {
    BotNotificationJob update(UUID id, Consumer<BotNotificationJob> change);
}
