package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroupBot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BotGroupBotRepository extends JpaRepository<BotGroupBot, Long> {

    List<BotGroupBot> findAllByGroupIdOrderByPlatformAscScopeKeyAsc(Long groupId);

    List<BotGroupBot> findAllByGroupIdInAndEnabledTrue(Collection<Long> groupIds);

    Optional<BotGroupBot> findByGroupIdAndPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
            Long groupId, String platform, String scopeType, String scopeKey, Long botInstanceId);

    Optional<BotGroupBot> findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
            String platform, String scopeType, String scopeKey, Long botInstanceId);

    List<BotGroupBot> findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
            String platform, String scopeType, String scopeKey, Long botInstanceId);

    void deleteAllByGroupId(Long groupId);
}
