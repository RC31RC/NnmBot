package com.nnm.bot.core.group.service;

import com.nnm.bot.core.group.repository.BotGroupMemberMergeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/** 合并后的主键解析只有一个实现；积分写入不使用可能过期的成员缓存。 */
@Component
public class CanonicalMemberResolver {
    @Resource
    private BotGroupMemberMergeRepository merges;

    public long resolve(long groupId, long memberId) {
        long current = memberId;
        for (int depth = 0; depth < 16; depth++) {
            long next = merges.findByGroupIdAndSourceMemberId(groupId, current)
                    .map(alias -> alias.getTargetMemberId()).orElse(current);
            if (next == current) return current;
            current = next;
        }
        throw new IllegalStateException("成员合并别名超过最大解析深度: " + memberId);
    }
}
