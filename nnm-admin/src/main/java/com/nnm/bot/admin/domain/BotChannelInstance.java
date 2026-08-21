package com.nnm.bot.admin.domain;

import com.nnm.bot.core.message.BotPlatform;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private BotPlatform platform;

    @Column(name = "instance_id", nullable = false, length = 100)
    private String instanceId;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "bot_account_id", nullable = false, length = 100)
    private String botAccountId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToOne(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    private OneBotChannelConfig oneBotConfig;

    @OneToOne(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    private QqOfficialChannelConfig qqOfficialConfig;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setOneBotConfig(OneBotChannelConfig config) {
        this.oneBotConfig = config;
        if (config != null) {
            config.setInstance(this);
        }
    }

    public void setQqOfficialConfig(QqOfficialChannelConfig config) {
        this.qqOfficialConfig = config;
        if (config != null) {
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
