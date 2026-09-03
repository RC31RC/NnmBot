package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommandTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotCommandTemplateRepository extends JpaRepository<BotCommandTemplate, Long> {

    List<BotCommandTemplate> findAllByCommandIdOrderBySortOrderAscIdAsc(Long commandId);

    long countByCommandId(Long commandId);
}
