package com.nnm.bot.admin.service.system.impl;

import com.nnm.bot.admin.constant.rbac.RbacErrorCode;
import cn.hutool.core.util.ObjUtil;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.dto.system.SessionDTO;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.system.OnlineSessionService;
import com.nnm.bot.admin.service.system.OperationAuditService;
import com.nnm.bot.common.error.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

/** 从 Sa-Token 会话存储枚举在线账号并执行会话下线。 */
@Service
public class OnlineSessionServiceImpl implements OnlineSessionService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private OperationAuditService auditService;

    @Override
    public List<SessionDTO> sessions() {
        StpLogic logic = StpUtil.getStpLogic();
        String prefix = logic.splicingKeyTokenValue("");
        return logic.searchTokenValue("", 0, -1, false).stream()
            .map(key -> key.startsWith(prefix) ? key.substring(prefix.length()) : key)
            .map(token -> toSession(logic, token)).filter(item -> ObjUtil.isNotNull(item))
            .sorted(Comparator.comparing(SessionDTO::loginTime).reversed()).toList();
    }

    @Override
    public void kickout(String token) {
        StpLogic logic = StpUtil.getStpLogic();
        Object loginId = logic.getLoginIdByToken(token);
        if (ObjUtil.isNull(loginId)) {
            throw new BusinessException(RbacErrorCode.ONLINE_SESSION_NOT_FOUND);
        }
        logic.kickoutByTokenValue(token);
        auditService.record("SESSION_KICKOUT", "SESSION", loginId, "强制下线指定会话");
    }

    private SessionDTO toSession(StpLogic logic, String token) {
        Object loginId = logic.getLoginIdByToken(token);
        if (ObjUtil.isNull(loginId)) {
            return null;
        }
        long userId;
        try {
            userId = Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException ignored) {
            return null;
        }
        SysUser user = userRepository.findById(userId).orElse(null);
        if (ObjUtil.isNull(user)) {
            return null;
        }

        SaTerminalInfo terminal = terminal(logic, loginId, token);
        LocalDateTime loginTime = ObjUtil.isNull(terminal) ? user.getUpdatedAt()
            : LocalDateTime.ofInstant(Instant.ofEpochMilli(terminal.getCreateTime()), ZoneId.systemDefault());
        return new SessionDTO(token, userId, user.getUsername(),
            ObjUtil.isNull(terminal) ? null : terminal.getDeviceType(), loginTime, logic.getTokenTimeout(token));
    }

    private SaTerminalInfo terminal(StpLogic logic, Object loginId, String token) {
        SaSession session = logic.getSessionByLoginId(loginId, false);
        if (ObjUtil.isNull(session)) {
            return null;
        }
        return session.getTerminalList().stream().filter(item -> token.equals(item.getTokenValue())).findFirst()
            .orElse(null);
    }
}
