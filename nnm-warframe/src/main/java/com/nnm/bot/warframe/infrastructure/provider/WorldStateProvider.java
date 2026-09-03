package com.nnm.bot.warframe.infrastructure.provider;

import cn.hutool.core.util.StrUtil;

import java.util.Optional;

/**
 * 外部世界状态数据源适配边界。
 *
 * <p>业务轮询只依赖统一响应，不感知具体第三方服务的 JSON 结构和地址。</p>
 */
public interface WorldStateProvider {

    Optional<WorldStatePayload> fetch();

    /**
     * 数据源返回的原始世界状态载荷。
     *
     * @param source 数据源编码
     * @param body 数据源返回的原始 JSON
     */
    record WorldStatePayload(String source, String body) {

        public WorldStatePayload {
            if (StrUtil.isBlank(source)) {
                throw new IllegalArgumentException("source must not be blank");
            }
            if (StrUtil.isBlank(body)) {
                throw new IllegalArgumentException("body must not be blank");
            }
        }
    }
}
