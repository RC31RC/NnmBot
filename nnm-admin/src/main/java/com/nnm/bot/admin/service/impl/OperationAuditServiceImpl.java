package com.nnm.bot.admin.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.domain.SysOperationLog;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.system.OperationLogItem;
import com.nnm.bot.admin.repository.SysOperationLogRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.common.response.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationAuditServiceImpl implements OperationAuditService {

    private final SysOperationLogRepository logRepository;
    private final SysUserRepository userRepository;

    @Override
    @Transactional
    public void record(String action, String targetType, Object targetId, String detail) {
        long operatorId = StpUtil.getLoginIdAsLong();
        String operatorName = userRepository.findById(operatorId)
                .map(SysUser::getUsername)
                .orElse("unknown");

        SysOperationLog log = new SysOperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId == null ? null : String.valueOf(targetId));
        log.setDetail(detail);
        log.setIpAddress(clientIp());
        logRepository.save(log);
    }

    @Override
    public PageResult<OperationLogItem> logs(String operatorName, int current, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(current, 1) - 1,
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<SysOperationLog> page = operatorName == null || operatorName.isBlank()
                ? logRepository.findAll(pageable)
                : logRepository.findByOperatorNameContainingIgnoreCase(operatorName.trim(), pageable);
        List<OperationLogItem> records = page.getContent().stream()
                .map(log -> new OperationLogItem(
                        log.getId(),
                        log.getOperatorId(),
                        log.getOperatorName(),
                        log.getAction(),
                        log.getTargetType(),
                        log.getTargetId(),
                        log.getDetail(),
                        log.getIpAddress(),
                        log.getCreatedAt()
                ))
                .toList();
        return PageResult.of(
                records,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements()
        );
    }

    private String clientIp() {
        Object source = SaHolder.getRequest().getSource();
        if (!(source instanceof HttpServletRequest request)) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
