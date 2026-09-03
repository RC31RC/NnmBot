package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotScopeRepository extends JpaRepository<BotScope, Long> {

    List<BotScope> findAllByOrderByPlatformAscScopeTypeAscScopeKeyAsc();

    java.util.Optional<BotScope> findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
            String platform, String scopeType, String scopeKey, Long botInstanceId);
}
