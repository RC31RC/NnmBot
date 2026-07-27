package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.system.SessionItem;

import java.util.List;

public interface OnlineSessionService {

    List<SessionItem> sessions();

    void kickout(String token);
}
