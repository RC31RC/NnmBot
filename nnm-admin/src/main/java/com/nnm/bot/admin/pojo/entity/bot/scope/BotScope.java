package com.nnm.bot.admin.pojo.entity.bot.scope;

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
@Table(name = "bot_scope")
public class BotScope {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 作用域所属平台。 */
    @Column(nullable = false, length = 32)
    private String platform;

    /** 作用域类型，例如 GROUP 或 PRIVATE。 */
    @Column(name = "scope_type", nullable = false, length = 20)
    private String scopeType;

    /** 平台侧作用域唯一标识。 */
    @Column(name = "scope_key", nullable = false, length = 160)
    private String scopeKey;

    /** 发现或绑定该作用域的机器人实例主键。 */
    @Column(name = "bot_instance_id", nullable = false)
    private Long botInstanceId;

    /** 作用域展示名称。 */
    @Column(name = "display_name", length = 160)
    private String displayName;

    /** 是否启用该作用域。 */
    @Column(nullable = false)
    private boolean enabled = true;
}
