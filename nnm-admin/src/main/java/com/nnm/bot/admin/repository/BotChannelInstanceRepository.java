package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotChannelInstance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface BotChannelInstanceRepository extends JpaRepository<BotChannelInstance, Long> {

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    List<BotChannelInstance> findAllByOrderByPlatformAscInstanceIdAsc();

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    Optional<BotChannelInstance> findByInstanceIdIgnoreCase(String instanceId);

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    Optional<BotChannelInstance> findByPlatformAndBotAccountId(
            com.nnm.bot.core.constant.BotPlatform platform, String botAccountId);

    /**
     * 串行化同一机器人实例的能力角色变更，避免并发请求重复插入唯一关联。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BotChannelInstance> findOneById(Long id);
}
