package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import java.util.List;
import java.util.UUID;

/** 提交、查询、取消并调度异步群发任务。 */
public interface BotNotificationJobService {

    /**
     * 创建异步群发任务。
     *
     * @param ownerId 发起用户主键
     * @param request 通知任务内容
     * @return 已创建任务
     */
    BotNotificationJobDTO submit(long ownerId, BotGroupNotificationDTO request);

    /**
     * 查询用户拥有的通知任务。
     *
     * @param ownerId 发起用户主键
     * @return 按创建时间排序的任务列表
     */
    List<BotNotificationJobDTO> list(long ownerId);

    /**
     * 取消用户拥有的待处理任务。
     *
     * @param ownerId 发起用户主键
     * @param id 任务标识
     * @return 更新后的任务状态
     */
    BotNotificationJobDTO cancel(long ownerId, UUID id);

    /** 认领并派发到期任务。 */
    void dispatchDue();
}
