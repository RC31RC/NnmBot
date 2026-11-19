package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/** 持久化命令回复所使用的模板内容。 */
public interface BotCommandTemplateRepository extends JpaRepository<BotCommandTemplate, Long> {}
