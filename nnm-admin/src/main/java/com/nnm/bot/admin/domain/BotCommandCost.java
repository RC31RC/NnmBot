package com.nnm.bot.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 命令在逻辑群组中的积分扣除与成功奖励配置。 */
@Getter
@Setter
@Entity
@Table(name = "bot_command_cost")
public class BotCommandCost {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属命令主键。 */
    @Column(name = "command_id", nullable = false, unique = true)
    private Long commandId;

    /** 每次成功执行需要消耗的群组积分。 */
    @Column(name = "cost_points", nullable = false)
    private long costPoints;

    /** 是否启用积分扣除。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 每次成功执行奖励的群组积分。 */
    @Column(name = "reward_points", nullable = false)
    private long rewardPoints;

    /** 是否启用成功奖励。 */
    @Column(name = "reward_enabled", nullable = false)
    private boolean rewardEnabled;
}
