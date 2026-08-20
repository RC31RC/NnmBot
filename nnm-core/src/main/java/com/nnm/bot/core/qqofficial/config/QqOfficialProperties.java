package com.nnm.bot.core.qqofficial.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** QQ 官方机器人渠道及其账号实例配置。 */
@ConfigurationProperties(prefix = "nnm.bot.channels.qq-official")
public class QqOfficialProperties {

    private boolean enabled;
    private List<Instance> instances = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances == null ? new ArrayList<>() : new ArrayList<>(instances);
    }

    public void validate() {
        if (!enabled) {
            return;
        }
        for (Instance instance : instances) {
            instance.validate();
        }
    }

    public static class Instance {

        private String id = "";
        private boolean enabled = true;
        private Connection connection = new Connection();
        private Runtime runtime = new Runtime();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection == null ? new Connection() : connection;
        }

        public Runtime getRuntime() {
            return runtime;
        }

        public void setRuntime(Runtime runtime) {
            this.runtime = runtime == null ? new Runtime() : runtime;
        }

        public String botAccountId() {
            return connection.appId;
        }

        void validate() {
            if (!enabled) {
                return;
            }
            requireText(id, "nnm.bot.channels.qq-official.instances[].id");
            requireText(connection.appId,
                    "nnm.bot.channels.qq-official.instances[].connection.app-id");
            requireText(connection.clientSecret,
                    "nnm.bot.channels.qq-official.instances[].connection.client-secret");
            requireText(connection.tokenUrl,
                    "nnm.bot.channels.qq-official.instances[].connection.token-url");
            requireText(connection.apiBaseUrl,
                    "nnm.bot.channels.qq-official.instances[].connection.api-base-url");
            if (runtime.intents < 0) {
                throw new IllegalArgumentException(
                        "nnm.bot.channels.qq-official.instances[].runtime.intents must not be negative");
            }
            if (runtime.gatewayConnectTimeout == null
                    || runtime.gatewayConnectTimeout.isNegative()
                    || runtime.gatewayConnectTimeout.isZero()) {
                throw new IllegalArgumentException(
                        "nnm.bot.channels.qq-official.instances[].runtime.gateway-connect-timeout must be positive");
            }
            if (runtime.reconnectDelay == null || runtime.reconnectDelay.isNegative()) {
                throw new IllegalArgumentException(
                        "nnm.bot.channels.qq-official.instances[].runtime.reconnect-delay must not be negative");
            }
        }
    }

    public static class Connection {

        private String appId = "";
        private String clientSecret = "";
        private String tokenUrl = "https://bots.qq.com/app/getAppAccessToken";
        private String apiBaseUrl = "https://api.sgroup.qq.com";
        private String gatewayUrl = "";

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }

        public void setTokenUrl(String tokenUrl) {
            this.tokenUrl = tokenUrl;
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        public void setApiBaseUrl(String apiBaseUrl) {
            this.apiBaseUrl = apiBaseUrl;
        }

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public void setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
        }
    }

    public static class Runtime {

        private long intents = (1L << 25) | (1L << 26);
        private Duration gatewayConnectTimeout = Duration.ofSeconds(10);
        private Duration reconnectDelay = Duration.ofSeconds(5);

        public long getIntents() {
            return intents;
        }

        public void setIntents(long intents) {
            this.intents = intents;
        }

        public Duration getGatewayConnectTimeout() {
            return gatewayConnectTimeout;
        }

        public void setGatewayConnectTimeout(Duration gatewayConnectTimeout) {
            this.gatewayConnectTimeout = gatewayConnectTimeout;
        }

        public Duration getReconnectDelay() {
            return reconnectDelay;
        }

        public void setReconnectDelay(Duration reconnectDelay) {
            this.reconnectDelay = reconnectDelay;
        }
    }

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(name + " must not be blank when enabled");
        }
    }
}
