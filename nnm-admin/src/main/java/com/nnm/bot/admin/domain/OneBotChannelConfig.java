package com.nnm.bot.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** OneBot 正向 WebSocket 专属配置。 */
@Getter
@Setter
@Entity
@Table(name = "bot_onebot_config")
public class OneBotChannelConfig {

    @Id
    @Column(name = "instance_id")
    private Long instanceId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instance_id")
    private BotChannelInstance instance;

    @Column(name = "connection_url", nullable = false, length = 500)
    private String connectionUrl;

    @Column(name = "access_token", length = 2000)
    private String accessToken;

    @Column(name = "reconnect_interval_seconds", nullable = false)
    private long reconnectIntervalSeconds = 5L;
}
