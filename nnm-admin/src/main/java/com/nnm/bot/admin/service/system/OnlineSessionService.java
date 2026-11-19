package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.SessionDTO;

import java.util.List;

/** 查询并管理当前在线的后台用户会话。 */
public interface OnlineSessionService {

    /** 查询在线会话列表。
     * @return 在线会话资料
     */
    List<SessionDTO> sessions();

    /** 强制注销指定令牌对应的会话。
     * @param token 会话令牌
     */
    void kickout(String token);
}
