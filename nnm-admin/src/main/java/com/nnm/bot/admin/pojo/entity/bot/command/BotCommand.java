package com.nnm.bot.admin.pojo.entity.bot.command;

import com.nnm.bot.core.plugin.model.PresentationPolicy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bot_command")
public class BotCommand {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 稳定的命令编码，供插件和数据库配置关联。 */
    @Column(nullable = false, unique = true, length = 80)
    private String code;

    /** 命令显示名称。 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 所属功能域或游戏编码。 */
    @Column(name = "game_code", nullable = false, length = 40)
    private String gameCode = "system";

    /** 命令说明。 */
    @Column(length = 255)
    private String description;

    /** 是否启用命令。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 是否显示在帮助菜单中。 */
    @Column(name = "show_in_help", nullable = false)
    private boolean showInHelp = true;

    /** 展示偏好、组合约束与交互策略，不控制必要内容是否发送。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "presentation", nullable = false, columnDefinition = "jsonb")
    private PresentationPolicy presentation = PresentationPolicy.defaults();

    /** 覆盖插件默认权限的系统权限码。 */
    @Column(name = "permission_override", length = 100)
    private String permissionOverride;

    /** 群组内最低角色要求：OWNER、ADMIN；为空表示不限制群组角色。 */
    @Column(name = "group_role_required", length = 20)
    private String groupRoleRequired;

    /** 同一调用者重复执行命令的冷却秒数。 */
    @Column(name = "cooldown_seconds", nullable = false)
    private long cooldownSeconds;

    /** 是否在支持的平台回复中提及命令触发者。 */
    @Column(name = "mention_invoker", nullable = false)
    private boolean mentionInvoker;

    /** 创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
