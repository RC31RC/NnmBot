package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotTemplateItem;
import com.nnm.bot.admin.dto.bot.BotTemplateSaveRequest;

import java.util.List;

public interface BotTemplateManagementService {

    List<BotTemplateItem> list();

    long create(BotTemplateSaveRequest request);

    void update(long id, BotTemplateSaveRequest request);

    void delete(long id);
}
