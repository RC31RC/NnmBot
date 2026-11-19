package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupMemberMerge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** 维护群成员合并记录与源成员关联。 */
public interface BotGroupMemberMergeRepository extends JpaRepository<BotGroupMemberMerge, Long> {

    void deleteAllByGroupId(Long groupId);

    Optional<BotGroupMemberMerge> findByGroupIdAndSourceMemberId(Long groupId, Long sourceMemberId);
}
