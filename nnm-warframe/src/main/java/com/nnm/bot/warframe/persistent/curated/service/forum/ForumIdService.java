package com.nnm.bot.warframe.persistent.curated.service.forum;

public interface ForumIdService {
    /**
     * 从分布式 ID 服务取得新的论坛记录主键。
     *
     * @return 新取得的唯一记录主键。
     */
    long next();
}
