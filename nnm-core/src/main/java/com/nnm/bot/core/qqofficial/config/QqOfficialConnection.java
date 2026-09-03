package com.nnm.bot.core.qqofficial.config;

/**
 * QQ 官方机器人运行期连接参数。
 *
 * <p>实例数据由管理模块从数据库读取后构造，连接器不再读取
 * {@code @ConfigurationProperties} 中的实例列表。</p>
 *
 * @param appId QQ 官方机器人应用 ID
 * @param clientSecret QQ 官方机器人应用密钥
 * @param tokenUrl 获取访问令牌的地址
 * @param apiBaseUrl QQ 官方机器人 API 基础地址
 * @param gatewayUrl QQ 官方机器人网关地址
 */
public record QqOfficialConnection(
        String appId,
        String clientSecret,
        String tokenUrl,
        String apiBaseUrl,
        String gatewayUrl) {
}
