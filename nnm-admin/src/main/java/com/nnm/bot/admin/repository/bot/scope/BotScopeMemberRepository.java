package com.nnm.bot.admin.repository.bot.scope;

import java.util.Optional;
import com.nnm.bot.admin.pojo.entity.bot.scope.BotScopeMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 查询平台作用域内同步的群成员身份与状态。 */
public interface BotScopeMemberRepository extends JpaRepository<BotScopeMember, Long> {

    List<BotScopeMember> findAllByScopeIdOrderByMemberRoleAscPlatformUserIdAsc(Long scopeId);

    long countByScopeId(Long scopeId);

    void deleteAllByScopeId(Long scopeId);

    Optional<BotScopeMember> findByScopeIdAndPlatformUserId(Long scopeId, String platformUserId);
}
