package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupMember;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BotGroupMemberRepository extends JpaRepository<BotGroupMember, Long> {

    List<BotGroupMember> findAllByGroupIdOrderByMemberRoleAscIdAsc(Long groupId);

    List<BotGroupMember> findAllByIdInAndEnabledTrue(Collection<Long> ids);

    long countByGroupId(Long groupId);

    Optional<BotGroupMember> findByGroupIdAndQqId(Long groupId, String qqId);

    Optional<BotGroupMember> findByGroupIdAndMemberOpenid(Long groupId, String memberOpenid);

    Optional<BotGroupMember> findByGroupIdAndKookId(Long groupId, String kookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BotGroupMember> findByIdAndGroupId(Long id, Long groupId);

    /** 只锁主键，不把数据库新版本合并到外层事务已加载的旧实体中。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m.id from BotGroupMember m where m.id = :id and m.groupId = :groupId")
    Optional<Long> lockIdByIdAndGroupId(Long id, Long groupId);

    void deleteAllByGroupId(Long groupId);
}
