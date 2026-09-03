package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommandChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotCommandChannelRepository extends JpaRepository<BotCommandChannel, Long> {

    Optional<BotCommandChannel> findByCommandIdAndPlatformIgnoreCase(Long commandId,
                                                                       String platform);

    List<BotCommandChannel> findAllByCommandId(Long commandId);
}
