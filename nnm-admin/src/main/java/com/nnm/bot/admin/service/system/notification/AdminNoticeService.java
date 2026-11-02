package com.nnm.bot.admin.service.system.notification;

import com.nnm.bot.admin.pojo.dto.system.notification.AdminNoticePublishDTO;
import com.nnm.bot.admin.pojo.vo.system.notification.AdminNoticeVO;
import com.nnm.bot.common.response.PageResult;
import java.time.Instant;
import java.util.List;

/** 管理端通知查询、发布及独立于浏览器的删除。 */
public interface AdminNoticeService {
    List<AdminNoticeVO> list(long userId);

    PageResult<AdminNoticeVO> history(long userId, int current, int size);

    void publish(long publisherId, AdminNoticePublishDTO request);

    void dismiss(long userId, String id);

    void dismissAll(long userId);

    void cleanup(Instant now);
}
