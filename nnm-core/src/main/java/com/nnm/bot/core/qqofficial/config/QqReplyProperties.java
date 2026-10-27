package com.nnm.bot.core.qqofficial.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import com.nnm.bot.core.constant.BotMessageScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.reply.qq")
public class QqReplyProperties {
    /** 为网络耗时预留的安全余量；不延长平台规定的回复窗口。 */
    private Duration safetyMargin = Duration.ofSeconds(5);
    /** 窗口结束后继续保留账本，避免延迟重复事件重置额度。 */
    private Duration ledgerRetention = Duration.ofHours(24);
    /** QQ 官方 C2C 被动回复的协议上限；这里只允许降低安全额度。 */
    private int c2cReplyLimit = 4;
    /** QQ 官方群聊被动回复的协议上限；这里只允许降低安全额度。 */
    private int groupReplyLimit = 5;

    /** 仅登记已确认获准使用原生键盘的账号和会话；空映射安全降级为命令文本。 */
    private Map<String, List<BotMessageScope>> keyboardAccounts = Map.of();
    /** 仅登记已由真实账号确认可主动投递的账号及群 OpenID；空映射默认关闭。 */
    private Map<String, List<String>> proactiveGroups = Map.of();

    public boolean keyboardAllowed(String account, BotMessageScope scope) {
        return keyboardAccounts.getOrDefault(account, List.of()).contains(scope);
    }

    public boolean proactiveGroupAllowed(String account, String groupOpenid) {
        return proactiveGroups.getOrDefault(account, List.of()).contains(groupOpenid);
    }

    @PostConstruct
    public void validate() {
        if (keyboardAccounts == null || keyboardAccounts.entrySet().stream().anyMatch(entry -> entry.getKey().isBlank()
                || entry.getValue() == null || entry.getValue().isEmpty() || entry.getValue().contains(BotMessageScope.CHANNEL))) {
            throw new IllegalArgumentException("Keyboard qualification must explicitly name GROUP or PRIVATE accounts");
        }
        if (proactiveGroups == null || proactiveGroups.entrySet().stream().anyMatch(entry -> entry.getKey().isBlank()
                || entry.getValue() == null || entry.getValue().isEmpty()
                || entry.getValue().stream().anyMatch(group -> group == null || group.isBlank()))) {
            throw new IllegalArgumentException("Proactive QQ delivery requires explicit account/group qualification");
        }
        if (safetyMargin == null || safetyMargin.isNegative()
                || safetyMargin.compareTo(Duration.ofMinutes(1)) >= 0
                || ledgerRetention == null || ledgerRetention.compareTo(Duration.ofHours(1)) < 0
                || c2cReplyLimit < 1 || c2cReplyLimit > 4
                || groupReplyLimit < 1 || groupReplyLimit > 5) {
            throw new IllegalArgumentException("Invalid nnm.reply.qq reply window, retention, or quota");
        }
    }
}
