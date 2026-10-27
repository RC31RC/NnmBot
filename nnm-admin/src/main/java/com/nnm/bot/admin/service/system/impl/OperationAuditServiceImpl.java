package com.nnm.bot.admin.service.system.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.entity.system.SysOperationLog;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.dto.system.OperationLogDTO;
import com.nnm.bot.admin.repository.system.SysOperationLogRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.system.OperationAuditService;
import com.nnm.bot.common.response.PageResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationAuditServiceImpl implements OperationAuditService {

    @Resource
    private SysOperationLogRepository logRepository;
    @Resource
    private SysUserRepository userRepository;

    @Override
    @Transactional
    public void record(String action, String targetType, Object targetId, String detail) {
        recordForOperator(StpUtil.getLoginIdAsLong(), action, targetType, targetId, detail);
    }

    @Override
    @Transactional
    public void recordForOperator(long operatorId, String action, String targetType, Object targetId, String detail) {
        String operatorName = userRepository.findById(operatorId)
                .map(SysUser::getUsername)
                .orElse("unknown");

        SysOperationLog log = new SysOperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(ObjectUtil.isNull(targetId) ? null : String.valueOf(targetId));
        log.setDetail(detail);
        log.setIpAddress(clientIp());
        logRepository.save(log);
    }

    @Override
    public PageResult<OperationLogDTO> logs(String operatorName, int current, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(current, 1) - 1,
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<SysOperationLog> page = StrUtil.isBlank(operatorName)
                ? logRepository.findAll(pageable)
                : logRepository.findByOperatorNameContainingIgnoreCase(operatorName.trim(), pageable);
        List<OperationLogDTO> records = page.getContent().stream()
                .map(log -> new OperationLogDTO(
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
        if (SaHolder.getRequest() == null) return null;
        Object source = SaHolder.getRequest().getSource();
        if (!(source instanceof HttpServletRequest request)) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
