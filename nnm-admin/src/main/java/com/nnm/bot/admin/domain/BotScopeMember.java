package com.nnm.bot.admin.domain;

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
@Table(name = "bot_scope_member")
public class BotScopeMember {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属平台作用域主键。 */
    @Column(name = "scope_id", nullable = false)
    private Long scopeId;

    /** 关联的后台系统用户主键。 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 平台侧成员标识。 */
    @Column(name = "platform_user_id", nullable = false, length = 160)
    private String platformUserId;

    /** 成员在该作用域中的角色。 */
    @Column(name = "member_role", nullable = false, length = 20)
    private String memberRole = "MEMBER";

    /** 是否启用该成员关系。 */
    @Column(nullable = false)
    private boolean enabled = true;
}
