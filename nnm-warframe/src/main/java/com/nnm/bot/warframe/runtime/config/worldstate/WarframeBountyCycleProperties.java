package com.nnm.bot.warframe.runtime.config.worldstate;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;

/** oracle.browse.wf 赏金轮换仅作为官方 PHP 缺失任务明细时的补充来源。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.bounty-cycle")
public class WarframeBountyCycleProperties {
    /** 是否允许读取补充接口；关闭后始终保留官方 PHP 原样解析结果。 */
    private boolean enabled = true;
    /** 社区补充接口地址，不参与官方静态成品导入。 */
    private URI url = URI.create("https://oracle.browse.wf/bounty-cycle");
    /** 该接口是很小的轮换 JSON，限制响应避免异常上游占用堆内存。 */
    private long maxBytes = 1_048_576L;

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (url == null || url.getHost() == null
                || !("https".equalsIgnoreCase(url.getScheme())
                || "http".equalsIgnoreCase(url.getScheme()))
                || maxBytes <= 0 || maxBytes > 8L * 1024 * 1024) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.bounty-cycle configuration");
        }
    }
}
