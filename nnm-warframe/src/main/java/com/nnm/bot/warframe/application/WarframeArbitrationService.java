package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.model.ArbitrationQuery;
import com.nnm.bot.warframe.model.ArbitrationScheduleResult;

public interface WarframeArbitrationService {
    ArbitrationScheduleResult query(ArbitrationQuery query);
}
