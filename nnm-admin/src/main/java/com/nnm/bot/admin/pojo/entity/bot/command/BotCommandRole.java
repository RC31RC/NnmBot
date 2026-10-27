package com.nnm.bot.admin.pojo.entity.bot.command;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bot_command_role")
public class BotCommandRole {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属命令主键。 */
    @Column(name = "command_id", nullable = false)
    private Long commandId;

    /** 可使用该命令的系统角色主键。 */
    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
