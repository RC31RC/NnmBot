package com.nnm.bot.admin.pojo.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作人系统用户主键。 */
    @Column(name = "operator_id")
    private Long operatorId;

    /** 操作人名称快照。 */
    @Column(name = "operator_name", nullable = false, length = 50)
    private String operatorName;

    /** 操作动作编码。 */
    @Column(nullable = false, length = 80)
    private String action;

    /** 被操作对象类型。 */
    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    /** 被操作对象标识。 */
    @Column(name = "target_id", length = 100)
    private String targetId;

    /** 操作详情或请求摘要。 */
    @Column(length = 1000)
    private String detail;

    /** 操作来源 IP 地址。 */
    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    /** 记录创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
