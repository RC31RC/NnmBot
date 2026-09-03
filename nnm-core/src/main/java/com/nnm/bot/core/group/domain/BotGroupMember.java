package com.nnm.bot.core.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 逻辑群组成员，不要求先成为后台系统用户。 */
@Getter
@Setter
@Entity
@Table(name = "bot_group_member")
public class BotGroupMember {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 逻辑群组主键。 */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /** OneBot 侧的 QQ 号，也是逻辑成员的主要跨渠道关联键。 */
    @Column(name = "qq_id", length = 32)
    private String qqId;

    /** QQ 官方机器人提供的群成员 OpenID。 */
    @Column(name = "member_openid", length = 160)
    private String memberOpenid;

    /** 未来 KOOK 渠道提供的用户 ID。 */
    @Column(name = "kook_id", length = 160)
    private String kookId;

    /** QQ 号是否已通过 OneBot 等可信渠道核验。 */
    @Column(name = "qq_id_verified", nullable = false)
    private boolean qqIdVerified;

    /** QQ 号绑定时间。 */
    @Column(name = "qq_bound_at")
    private LocalDateTime qqBoundAt;

    /** 平台侧昵称快照。 */
    @Column(name = "display_name", length = 160)
    private String displayName;

    /** 成员在逻辑群组中的角色。 */
    @Column(name = "member_role", nullable = false, length = 20)
    private String memberRole = "MEMBER";

    /** 是否允许该成员参与群组功能。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 当前可用积分余额。 */
    @Column(nullable = false)
    private long points;

    /** 累计获得积分。 */
    @Column(name = "total_earned", nullable = false)
    private long totalEarned;

    /** 累计消耗积分。 */
    @Column(name = "total_spent", nullable = false)
    private long totalSpent;

    /** 乐观锁版本号，避免并发积分更新覆盖。 */
    @Version
    @Column(nullable = false)
    private int version;
}
