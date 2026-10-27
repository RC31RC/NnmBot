package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupMemberMerge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotGroupMemberMergeRepository extends JpaRepository<BotGroupMemberMerge, Long> {

    Optional<BotGroupMemberMerge> findByGroupIdAndSourceMemberId(
            Long groupId, Long sourceMemberId);
}
