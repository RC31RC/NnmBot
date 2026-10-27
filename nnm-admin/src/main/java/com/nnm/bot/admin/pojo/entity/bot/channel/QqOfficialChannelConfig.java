package com.nnm.bot.admin.pojo.entity.bot.channel;

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

    /** 与渠道实例共用的主键。 */
    @Id
    @Column(name = "instance_id")
    private Long instanceId;

    /** 对应的渠道实例。 */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instance_id")
    private BotChannelInstance instance;

    /** QQ 官方机器人应用 ID。 */
    @Column(name = "app_id", nullable = false, length = 200)
    private String appId;

    /** QQ 官方机器人应用密钥。 */
    @Column(name = "client_secret", nullable = false, length = 2000)
    private String clientSecret;

    /** 获取访问令牌的接口地址。 */
    @Column(name = "token_url", nullable = false, length = 500)
    private String tokenUrl;

    /** QQ 官方机器人 API 基础地址。 */
    @Column(name = "api_base_url", nullable = false, length = 500)
    private String apiBaseUrl;

    /** 可选的网关地址。 */
    @Column(name = "gateway_url", length = 500)
    private String gatewayUrl;

    /** 订阅的事件意图位掩码。 */
    @Column(name = "intents", nullable = false)
    private long intents = 117440512L;

    /** 网关建立连接的超时秒数。 */
    @Column(name = "gateway_connect_timeout_seconds", nullable = false)
    private long gatewayConnectTimeoutSeconds = 10L;

    /** 网关断开后的重连延迟秒数。 */
    @Column(name = "reconnect_delay_seconds", nullable = false)
    private long reconnectDelaySeconds = 5L;
}
