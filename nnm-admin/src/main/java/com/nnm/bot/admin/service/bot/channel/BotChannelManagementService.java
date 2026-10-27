package com.nnm.bot.admin.service.bot.channel;

import com.nnm.bot.admin.pojo.dto.bot.channel.BotChannelInstanceDTO;
import com.nnm.bot.admin.pojo.query.bot.channel.OneBotAccountProbeQuery;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotAccountProbeDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotInstanceSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.QqOfficialInstanceSaveDTO;

import java.util.List;

public interface BotChannelManagementService {

    List<BotChannelInstanceDTO> instances();

    long createOneBot(OneBotInstanceSaveDTO request);

    void updateOneBot(long id, OneBotInstanceSaveDTO request);

    long createQqOfficial(QqOfficialInstanceSaveDTO request);

    void updateQqOfficial(long id, QqOfficialInstanceSaveDTO request);

    void delete(long id);

    void start(long id);

    void stop(long id);

    OneBotAccountProbeDTO probeOneBot(OneBotAccountProbeQuery request);
}
