package com.nnm.bot.admin.pojo.entity.bot.command;

import jakarta.persistence.Column;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Getter
@Setter
@Entity
@Check(constraints = "sort_order >= 0")
@Table(name = "bot_command_group_member", uniqueConstraints = {
        @UniqueConstraint(name = "uq_bot_command_group_member_command", columnNames = "command_code")
})
public class BotCommandGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "command_code", nullable = false, unique = true, length = 80)
    private String commandCode;

    @Column(nullable = false, length = 100)
    private String title;

    /** 目标参数名到来源白名单上下文键；不支持表达式或常量拼接。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameter_bindings", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> parameterBindings = Map.of();

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
