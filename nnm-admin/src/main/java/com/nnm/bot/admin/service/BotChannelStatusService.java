package com.nnm.bot.admin.service;

import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import java.util.List;

public interface BotChannelStatusService {
    List<BotChannelSnapshot> snapshots();

    default boolean isDistributed() { return false; }
}
