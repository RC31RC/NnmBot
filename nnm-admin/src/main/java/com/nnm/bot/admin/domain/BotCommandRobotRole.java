package com.nnm.bot.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 命令与机器人能力角色的关联。 */
@Getter
@Setter
@Entity
@Table(name = "bot_command_robot_role")
public class BotCommandRobotRole {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属命令主键。 */
    @Column(name = "command_id", nullable = false)
    private Long commandId;

    /** 所属机器人能力角色主键。 */
    @Column(name = "robot_role_id", nullable = false)
    private Long robotRoleId;
}
