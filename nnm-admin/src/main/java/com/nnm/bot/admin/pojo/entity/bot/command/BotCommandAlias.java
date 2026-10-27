package com.nnm.bot.admin.pojo.entity.bot.command;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bot_command_alias")
public class BotCommandAlias {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属命令主键。 */
    @Column(name = "command_id", nullable = false)
    private Long commandId;

    /** 可用于匹配命令的别名。 */
    @Column(name = "alias_name", nullable = false, length = 100)
    private String aliasName;

    /** 前缀命令触发词与参数之间的边界策略。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "boundary_policy", nullable = false, length = 30)
    private CommandBoundaryPolicy boundaryPolicy;
}
