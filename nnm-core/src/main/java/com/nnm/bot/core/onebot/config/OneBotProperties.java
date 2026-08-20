package com.nnm.bot.core.onebot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** OneBot 渠道及其连接实例配置。 */
@ConfigurationProperties(prefix = "nnm.bot.channels.onebot")
public class OneBotProperties {

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
        private String botAccountId = "";
        private boolean enabled = true;
        private Connection connection = new Connection();
        private Runtime runtime = new Runtime();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBotAccountId() {
            return botAccountId;
        }

        public void setBotAccountId(String botAccountId) {
            this.botAccountId = botAccountId;
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

        void validate() {
            if (!enabled) {
                return;
            }
            requireText(id, "nnm.bot.channels.onebot.instances[].id");
            requireText(botAccountId,
                    "nnm.bot.channels.onebot.instances[].bot-account-id");
            try {
                Long.parseLong(botAccountId);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "nnm.bot.channels.onebot.instances[].bot-account-id must be numeric", exception);
            }
            requireText(connection.url, "nnm.bot.channels.onebot.instances[].connection.url");
            if (runtime.reconnectInterval == null || runtime.reconnectInterval.isNegative()
                    || runtime.reconnectInterval.isZero()) {
                throw new IllegalArgumentException(
                        "nnm.bot.channels.onebot.instances[].runtime.reconnect-interval must be positive");
            }
        }
    }

    public static class Connection {

        private String url = "";
        private String accessToken = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    public static class Runtime {

        private Duration reconnectInterval = Duration.ofSeconds(5);

        public Duration getReconnectInterval() {
            return reconnectInterval;
        }

        public void setReconnectInterval(Duration reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
        }
    }

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(name + " must not be blank when enabled");
        }
    }
}
