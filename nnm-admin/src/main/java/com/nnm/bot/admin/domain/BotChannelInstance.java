package com.nnm.bot.admin.domain;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.constant.BotPlatform;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 机器人渠道实例的协议无关信息。 */
@Getter
@Setter
@Entity
@Table(name = "bot_channel_instance")
public class BotChannelInstance {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接入平台，例如 OneBot 或 QQ 官方机器人。 */
    @Column(name = "platform", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private BotPlatform platform;

    /** 系统内部使用的实例编码。 */
    @Column(name = "instance_id", nullable = false, length = 100)
    private String instanceId;

    /** 管理端展示名称。 */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** 平台侧机器人账号标识。 */
    @Column(name = "bot_account_id", nullable = false, length = 100)
    private String botAccountId;

    /** 是否允许运行时启动该实例。 */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /** OneBot 专属连接配置。 */
    @OneToOne(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    private OneBotChannelConfig oneBotConfig;

    /** QQ 官方机器人专属连接配置。 */
    @OneToOne(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    private QqOfficialChannelConfig qqOfficialConfig;

    /** 创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setOneBotConfig(OneBotChannelConfig config) {
        this.oneBotConfig = config;
        if (ObjectUtil.isNotNull(config)) {
            config.setInstance(this);
        }
    }

    public void setQqOfficialConfig(QqOfficialChannelConfig config) {
        this.qqOfficialConfig = config;
        if (ObjectUtil.isNotNull(config)) {
            config.setInstance(this);
        }
    }

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
