package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.SysOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** 分页读取后台操作审计记录。 */
public interface SysOperationLogRepository extends JpaRepository<SysOperationLog, Long> {

    Page<SysOperationLog> findByOperatorNameContainingIgnoreCase(String operatorName, Pageable pageable);
}
