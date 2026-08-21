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

/** QQ 官方机器人专属配置。 */
@Getter
@Setter
@Entity
@Table(name = "bot_qq_official_config")
public class QqOfficialChannelConfig {

    @Id
    @Column(name = "instance_id")
    private Long instanceId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instance_id")
    private BotChannelInstance instance;

    @Column(name = "app_id", nullable = false, length = 200)
    private String appId;

    @Column(name = "client_secret", nullable = false, length = 2000)
    private String clientSecret;

    @Column(name = "token_url", nullable = false, length = 500)
    private String tokenUrl;

    @Column(name = "api_base_url", nullable = false, length = 500)
    private String apiBaseUrl;

    @Column(name = "gateway_url", length = 500)
    private String gatewayUrl;

    @Column(name = "intents", nullable = false)
    private long intents = 100663296L;

    @Column(name = "gateway_connect_timeout_seconds", nullable = false)
    private long gatewayConnectTimeoutSeconds = 10L;

    @Column(name = "reconnect_delay_seconds", nullable = false)
    private long reconnectDelaySeconds = 5L;
}
