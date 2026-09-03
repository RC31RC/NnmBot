package com.nnm.bot.core.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 群组积分流水，保留扣费和退款的审计轨迹。 */
@Getter
@Setter
@Entity
@Table(name = "bot_group_point_transaction")
public class BotGroupPointTransaction {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 逻辑群组主键。 */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /** 群组成员主键。 */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /** 触发积分变化的命令编码。 */
    @Column(name = "command_code", length = 80)
    private String commandCode;

    /** 流水类型，例如签到、扣费或退款。 */
    @Column(name = "transaction_type", nullable = false, length = 32)
    private String transactionType;

    /** 本次变化数量，收入为正、支出为负。 */
    @Column(nullable = false)
    private long amount;

    /** 变更后的成员积分余额。 */
    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    /** 防止重复扣费或重复记账的幂等键。 */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 160)
    private String idempotencyKey;

    /** 流水说明。 */
    @Column(length = 255)
    private String description;

    /** 流水创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
