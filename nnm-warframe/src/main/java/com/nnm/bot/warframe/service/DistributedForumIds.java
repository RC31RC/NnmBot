package com.nnm.bot.warframe.service;

import com.nnm.bot.warframe.repository.WarframeForumArticleRepository;
import io.github.rdt.id.DistributedIdGenerator;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DistributedForumIds {
    @Resource
    private WarframeForumArticleRepository repository;

    @Resource
    private DistributedIdGenerator ids;

    public long next() {
        long maximum = repository.findFirstByOrderByIdDesc().map(article -> article.getId()).orElse(0L);
        return ids.nextAtLeast("warframe-forum-article-v1", Math.addExact(maximum, 1));
    }
}
