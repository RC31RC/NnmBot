package com.nnm.bot.admin.pojo.entity.bot.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 机器人能力角色与机器人实例的关联。 */
@Getter
@Setter
@Entity
@Table(name = "bot_robot_role_instance")
public class BotRobotRoleInstance {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机器人能力角色主键。 */
    @Column(name = "robot_role_id", nullable = false)
    private Long robotRoleId;

    /** 机器人渠道实例主键。 */
    @Column(name = "bot_instance_id", nullable = false)
    private Long botInstanceId;
}
