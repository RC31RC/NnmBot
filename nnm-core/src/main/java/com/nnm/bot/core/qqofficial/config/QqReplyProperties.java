package com.nnm.bot.core.qqofficial.config;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.collection.CollUtil;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import com.nnm.bot.core.messaging.constant.BotMessageScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** QQ 官方回复窗口、额度和经人工确认的能力白名单。 */
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

    /**
     * 判断账号和会话是否经过原生键盘能力确认。
     *
     * @param account 机器人账号标识
     * @param scope 目标消息作用域
     * @return 配置显式允许时为 {@code true}
     */
    public boolean keyboardAllowed(String account, BotMessageScope scope) {
        return keyboardAccounts.getOrDefault(account, List.of()).contains(scope);
    }

    /**
     * 判断账号是否获准向指定群 OpenID 主动发送。
     *
     * @param account 机器人账号标识
     * @param groupOpenid 目标群 OpenID
     * @return 配置显式允许时为 {@code true}
     */
    public boolean proactiveGroupAllowed(String account, String groupOpenid) {
        return proactiveGroups.getOrDefault(account, List.of()).contains(groupOpenid);
    }

    /**
     * 校验回复额度、保留时间及主动发送白名单。
     *
     * @throws IllegalArgumentException 配置缺少显式资格或超出平台安全范围
     */
    @PostConstruct
    public void validate() {
        if (keyboardAccounts == null
            || keyboardAccounts.entrySet().stream().anyMatch(entry -> CharSequenceUtil.isBlank(entry.getKey())
                || CollUtil.isEmpty(entry.getValue()) || entry.getValue().contains(BotMessageScope.CHANNEL))) {
            throw new IllegalArgumentException("Keyboard qualification must explicitly name GROUP or PRIVATE accounts");
        }
        if (proactiveGroups == null || proactiveGroups.entrySet().stream()
            .anyMatch(entry -> CharSequenceUtil.isBlank(entry.getKey()) || CollUtil.isEmpty(entry.getValue())
                || entry.getValue().stream().anyMatch(group -> group == null || CharSequenceUtil.isBlank(group)))) {
            throw new IllegalArgumentException("Proactive QQ delivery requires explicit account/group qualification");
        }
        if (safetyMargin == null || safetyMargin.isNegative() || safetyMargin.compareTo(Duration.ofMinutes(1)) >= 0
            || ledgerRetention == null || ledgerRetention.compareTo(Duration.ofHours(1)) < 0 || c2cReplyLimit < 1
            || c2cReplyLimit > 4 || groupReplyLimit < 1 || groupReplyLimit > 5) {
            throw new IllegalArgumentException("Invalid nnm.reply.qq reply window, retention, or quota");
        }
    }
}
