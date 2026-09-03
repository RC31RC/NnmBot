package com.nnm.bot.core.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 逻辑群组与平台侧机器人作用域的关联。 */
@Getter
@Setter
@Entity
@Table(name = "bot_group_bot")
public class BotGroupBot {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 逻辑群组主键。 */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /** 绑定的机器人实例主键。 */
    @Column(name = "bot_instance_id", nullable = false)
    private Long botInstanceId;

    /** 机器人所属平台。 */
    @Column(nullable = false, length = 32)
    private String platform;

    /** 平台作用域类型。 */
    @Column(name = "scope_type", nullable = false, length = 20)
    private String scopeType = "GROUP";

    /** 平台侧群组或频道标识。 */
    @Column(name = "scope_key", nullable = false, length = 160)
    private String scopeKey;

    /** 平台侧作用域名称快照。 */
    @Column(name = "display_name", length = 160)
    private String displayName;

    /** 是否启用该机器人在群组中的绑定。 */
    @Column(nullable = false)
    private boolean enabled = true;
}
