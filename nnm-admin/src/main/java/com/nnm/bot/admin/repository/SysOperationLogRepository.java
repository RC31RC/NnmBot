package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.SysOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysOperationLogRepository extends JpaRepository<SysOperationLog, Long> {

    Page<SysOperationLog> findByOperatorNameContainingIgnoreCase(String operatorName, Pageable pageable);
}
