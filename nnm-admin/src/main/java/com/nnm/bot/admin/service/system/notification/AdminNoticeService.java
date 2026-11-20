package com.nnm.bot.admin.service.system.notification;

import com.nnm.bot.admin.pojo.dto.system.notification.AdminNoticePublishDTO;
import com.nnm.bot.admin.pojo.vo.system.notification.AdminNoticeVO;
import com.nnm.bot.common.response.PageResult;
import java.time.Instant;
import java.util.List;

/** 管理端通知查询、发布及独立于浏览器的删除。 */
public interface AdminNoticeService {
    /**
     * 查询用户当前未关闭的通知。
     *
     * @param userId 用户主键
     * @return 当前通知列表
     */
    List<AdminNoticeVO> list(long userId);

    /**
     * 分页查询用户可见的历史通知。
     *
     * @param userId 用户主键
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @return 历史通知分页结果
     */
    PageResult<AdminNoticeVO> history(long userId, int current, int size);

    /**
     * 发布管理端通知。
     *
     * @param publisherId 发布人主键
     * @param request 通知内容和目标范围
     */
    void publish(long publisherId, AdminNoticePublishDTO request);

    /**
     * 关闭用户的一条通知。
     *
     * @param userId 用户主键
     * @param id 通知标识
     */
    void dismiss(long userId, String id);

    /**
     * 关闭用户当前全部通知。
     *
     * @param userId 用户主键
     */
    void dismissAll(long userId);

    /**
     * 清理指定时刻之前已过期的通知。
     *
     * @param now 清理基准时刻
     */
    void cleanup(Instant now);
}
