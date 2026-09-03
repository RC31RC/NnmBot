package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommandAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BotCommandAliasRepository extends JpaRepository<BotCommandAlias, Long> {

    List<BotCommandAlias> findAllByCommandId(Long commandId);

    @Modifying
    @Transactional
    @Query("delete from BotCommandAlias alias where alias.commandId = :commandId")
    int deleteAllByCommandId(Long commandId);
}
