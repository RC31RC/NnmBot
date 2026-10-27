package com.nnm.bot.warframe.persistent.curated.service.forum.impl;

import com.nnm.bot.warframe.persistent.curated.service.forum.ForumIdService;
import com.nnm.bot.warframe.persistent.curated.repository.forum.ForumArticleRepository;
import io.github.rdt.id.DistributedIdGenerator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ForumIdServiceImpl implements ForumIdService {
    @Resource
    private ForumArticleRepository repository;

    @Resource
    private DistributedIdGenerator ids;

    /**
     * 从分布式 ID 服务取得新的论坛记录主键。
     *
     * @return 新取得的唯一记录主键。
     */
    public long next() {
        // 使用 Redis ATOMIC 序列，并以数据库已有最大 ID 为包含式下界；不依赖本 JVM 的计数器。
        long maximum = repository.findFirstProjectedByOrderByIdDesc().map(article -> article.getId()).orElse(0L);
        return ids.nextAtLeast("warframe-forum-article-v1", Math.addExact(maximum, 1));
    }
}
