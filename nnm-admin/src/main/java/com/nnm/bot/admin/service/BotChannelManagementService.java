package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotChannelInstanceItem;
import com.nnm.bot.admin.dto.bot.OneBotAccountProbeRequest;
import com.nnm.bot.admin.dto.bot.OneBotAccountProbeResponse;
import com.nnm.bot.admin.dto.bot.OneBotInstanceSaveRequest;
import com.nnm.bot.admin.dto.bot.QqOfficialInstanceSaveRequest;

import java.util.List;

public interface BotChannelManagementService {

    List<BotChannelInstanceItem> instances();

    long createOneBot(OneBotInstanceSaveRequest request);

    void updateOneBot(long id, OneBotInstanceSaveRequest request);

    long createQqOfficial(QqOfficialInstanceSaveRequest request);

    void updateQqOfficial(long id, QqOfficialInstanceSaveRequest request);

    void delete(long id);

    OneBotAccountProbeResponse probeOneBot(OneBotAccountProbeRequest request);
}
