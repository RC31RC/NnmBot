package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import java.util.List;
import java.util.UUID;

public interface BotNotificationJobService {
    BotNotificationJobDTO submit(long ownerId, BotGroupNotificationDTO request);
    List<BotNotificationJobDTO> list(long ownerId);
    BotNotificationJobDTO cancel(long ownerId, UUID id);
    void dispatchDue();
}
