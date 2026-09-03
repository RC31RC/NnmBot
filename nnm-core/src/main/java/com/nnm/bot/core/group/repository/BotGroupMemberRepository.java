package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BotGroupMemberRepository extends JpaRepository<BotGroupMember, Long> {

    List<BotGroupMember> findAllByGroupIdOrderByMemberRoleAscIdAsc(Long groupId);

    List<BotGroupMember> findAllByIdInAndEnabledTrue(Collection<Long> ids);

    long countByGroupId(Long groupId);

    Optional<BotGroupMember> findByGroupIdAndQqId(Long groupId, String qqId);

    Optional<BotGroupMember> findByGroupIdAndMemberOpenid(
            Long groupId, String memberOpenid);

    Optional<BotGroupMember> findByGroupIdAndKookId(Long groupId, String kookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BotGroupMember> findWithLockById(Long id);

    void deleteAllByGroupId(Long groupId);
}
