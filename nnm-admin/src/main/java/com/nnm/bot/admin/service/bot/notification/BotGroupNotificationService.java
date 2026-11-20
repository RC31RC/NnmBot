package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.core.task.service.TaskProgressReporter;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationPreviewDTO;
import java.util.List;

/** 预览并向群组成员发送通知。 */
public interface BotGroupNotificationService {

    /**
     * 按群组和机器人绑定发送通知。
     *
     * @param request 通知内容与目标群组
     * @param manifest 本次任务使用的资源清单
     * @param reporter 任务进度接收器
     * @return 各群组的发送结果
     * @throws InterruptedException 当前线程在发送期间被中断
     */
    BotGroupNotificationReportDTO send(BotGroupNotificationDTO request, NotificationResourceManifest manifest,
        TaskProgressReporter reporter) throws InterruptedException;

    /**
     * 生成发送目标及内容预览，不执行发送。
     *
     * @param ownerId 发起用户主键
     * @param request 通知内容与目标群组
     * @return 按群组生成的预览列表
     */
    List<BotNotificationPreviewDTO> preview(long ownerId, BotGroupNotificationDTO request);
}
