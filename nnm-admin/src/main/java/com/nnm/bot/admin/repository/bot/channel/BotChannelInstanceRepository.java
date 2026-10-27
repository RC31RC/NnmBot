package com.nnm.bot.admin.repository.bot.channel;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.core.constant.BotPlatform;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotChannelInstanceRepository extends JpaRepository<BotChannelInstance, Long> {

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    List<BotChannelInstance> findAllByOrderByPlatformAscInstanceIdAsc();

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    Optional<BotChannelInstance> findByInstanceIdIgnoreCase(String instanceId);

    @EntityGraph(attributePaths = {"oneBotConfig", "qqOfficialConfig"})
    Optional<BotChannelInstance> findByPlatformAndBotAccountId(
            BotPlatform platform, String botAccountId);

}
