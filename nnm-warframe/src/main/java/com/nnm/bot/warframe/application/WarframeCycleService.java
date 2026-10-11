package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.model.WorldState;

public interface WarframeCycleService {
    WorldState resolve(WorldState state);
}
