package com.nnm.bot.admin.pojo.entity.system;

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
@Table(name = "sys_user_identity")
public class SysUserIdentity {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的系统用户主键。 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 外部平台编码，例如 QQ 或 KOOK。 */
    @Column(nullable = false, length = 32)
    private String platform;

    /** 外部平台侧用户标识。 */
    @Column(name = "external_user_id", nullable = false, length = 160)
    private String externalUserId;
}
