package com.nnm.bot.warframe.infrastructure.provider;

import java.util.Optional;

/**
 * 外部世界状态数据源适配边界。
 *
 * <p>业务轮询只依赖统一响应，不感知具体第三方服务的 JSON 结构和地址。</p>
 */
public interface WorldStateProvider {

    Optional<WorldStatePayload> fetch();

    record WorldStatePayload(String source, String body) {

        public WorldStatePayload {
            if (source == null || source.isBlank()) {
                throw new IllegalArgumentException("source must not be blank");
            }
            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("body must not be blank");
            }
        }
    }
}
