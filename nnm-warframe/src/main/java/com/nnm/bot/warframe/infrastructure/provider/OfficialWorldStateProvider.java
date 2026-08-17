package com.nnm.bot.warframe.infrastructure.provider;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Warframe 官方 WorldState 数据源适配器。
 */
@Component
public class OfficialWorldStateProvider implements WorldStateProvider {

    static final String SOURCE = "warframe-official";

    private final WarframeApiClient apiClient;

    public OfficialWorldStateProvider(WarframeApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Optional<WorldStatePayload> fetch() {
        HttpClient.HttpResult result = apiClient.fetchWorldStateResult();
        if (!result.isSuccess() || result.body() == null || result.body().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new WorldStatePayload(SOURCE, result.body()));
    }
}
