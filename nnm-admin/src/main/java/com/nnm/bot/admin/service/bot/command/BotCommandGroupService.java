package com.nnm.bot.admin.service.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupSaveDTO;
import java.util.List;
import com.nnm.bot.admin.pojo.query.bot.command.BotCommandGroupPreviewQuery;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupPreviewDTO;

public interface BotCommandGroupService {
    List<BotCommandGroupDTO> list();

    List<BotCommandGroupPreviewDTO> preview(long id, BotCommandGroupPreviewQuery request);

    long save(BotCommandGroupSaveDTO request);

    void delete(long id, int version);
}
