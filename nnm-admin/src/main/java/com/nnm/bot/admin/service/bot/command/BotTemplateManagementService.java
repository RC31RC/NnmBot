package com.nnm.bot.admin.service.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateSaveDTO;

import java.util.List;

public interface BotTemplateManagementService {

    List<BotTemplateDTO> list();

    long create(BotTemplateSaveDTO request);

    void update(long id, BotTemplateSaveDTO request);

    void delete(long id);
}
