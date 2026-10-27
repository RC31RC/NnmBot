package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.SessionDTO;

import java.util.List;

public interface OnlineSessionService {

    List<SessionDTO> sessions();

    void kickout(String token);
}
