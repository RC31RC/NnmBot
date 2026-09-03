package com.nnm.bot.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.system.SessionItem;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.service.OnlineSessionService;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class OnlineSessionServiceImpl implements OnlineSessionService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private OperationAuditService auditService;

    @Override
    public List<SessionItem> sessions() {
        StpLogic logic = StpUtil.getStpLogic();
        String prefix = logic.splicingKeyTokenValue("");
        return logic.searchTokenValue("", 0, -1, false).stream()
                .map(key -> key.startsWith(prefix) ? key.substring(prefix.length()) : key)
                .map(token -> toSession(logic, token))
                .filter(item -> ObjectUtil.isNotNull(item))
                .sorted(Comparator.comparing(SessionItem::loginTime).reversed())
                .toList();
    }

    @Override
    public void kickout(String token) {
        StpLogic logic = StpUtil.getStpLogic();
        Object loginId = logic.getLoginIdByToken(token);
        if (ObjectUtil.isNull(loginId)) {
            throw new BusinessException(404, "在线会话不存在");
        }
        logic.kickoutByTokenValue(token);
        auditService.record("SESSION_KICKOUT", "SESSION", loginId, "强制下线指定会话");
    }

    private SessionItem toSession(StpLogic logic, String token) {
        Object loginId = logic.getLoginIdByToken(token);
        if (ObjectUtil.isNull(loginId)) {
            return null;
        }
        long userId;
        try {
            userId = Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException ignored) {
            return null;
        }
        SysUser user = userRepository.findById(userId).orElse(null);
        if (ObjectUtil.isNull(user)) {
            return null;
        }

        SaTerminalInfo terminal = terminal(logic, loginId, token);
        LocalDateTime loginTime = ObjectUtil.isNull(terminal)
                ? user.getUpdatedAt()
                : LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(terminal.getCreateTime()),
                        ZoneId.systemDefault()
                );
        return new SessionItem(
                token,
                userId,
                user.getUsername(),
                ObjectUtil.isNull(terminal) ? null : terminal.getDeviceType(),
                loginTime,
                logic.getTokenTimeout(token)
        );
    }

    private SaTerminalInfo terminal(StpLogic logic, Object loginId, String token) {
        SaSession session = logic.getSessionByLoginId(loginId, false);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return session.getTerminalList().stream()
                .filter(item -> token.equals(item.getTokenValue()))
                .findFirst()
                .orElse(null);
    }
}
