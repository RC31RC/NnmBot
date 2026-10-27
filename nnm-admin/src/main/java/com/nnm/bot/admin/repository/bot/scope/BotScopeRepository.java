package com.nnm.bot.admin.repository.bot.scope;

import java.util.Optional;
import com.nnm.bot.admin.pojo.entity.bot.scope.BotScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotScopeRepository extends JpaRepository<BotScope, Long> {

    List<BotScope> findAllByOrderByPlatformAscScopeTypeAscScopeKeyAsc();

    Optional<BotScope> findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
            String platform, String scopeType, String scopeKey, Long botInstanceId);
}
