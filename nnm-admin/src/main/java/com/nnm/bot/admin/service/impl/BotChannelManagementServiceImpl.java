package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.domain.BotChannelInstance;
import com.nnm.bot.admin.domain.OneBotChannelConfig;
import com.nnm.bot.admin.domain.QqOfficialChannelConfig;
import com.nnm.bot.admin.dto.bot.BotChannelInstanceItem;
import com.nnm.bot.admin.dto.bot.OneBotAccountProbeRequest;
import com.nnm.bot.admin.dto.bot.OneBotAccountProbeResponse;
import com.nnm.bot.admin.dto.bot.OneBotInstanceSaveRequest;
import com.nnm.bot.admin.dto.bot.QqOfficialInstanceSaveRequest;
import com.nnm.bot.admin.error.BotManagementErrorCode;
import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.admin.service.BotChannelManagementService;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.channel.BotChannelRegistry;
import com.nnm.bot.core.channel.BotChannelSnapshot;
import com.nnm.bot.core.message.BotPlatform;
import com.nnm.bot.core.onebot.OneBotAccountProbe;
import com.nnm.bot.core.onebot.OneBotAccountProbeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BotChannelManagementServiceImpl implements BotChannelManagementService {

    private final BotChannelInstanceRepository repository;
    private final BotChannelRegistry registry;
    private final OneBotAccountProbe accountProbe;
    private final BotCredentialCipher credentialCipher;
    private final OperationAuditService auditService;

    public BotChannelManagementServiceImpl(
            BotChannelInstanceRepository repository,
            BotChannelRegistry registry,
            OneBotAccountProbe accountProbe,
            BotCredentialCipher credentialCipher,
            OperationAuditService auditService) {
        this.repository = repository;
        this.registry = registry;
        this.accountProbe = accountProbe;
        this.credentialCipher = credentialCipher;
        this.auditService = auditService;
    }

    @Override
    public List<BotChannelInstanceItem> instances() {
        return repository.findAllByOrderByPlatformAscInstanceIdAsc().stream()
                .map(this::toItem)
                .toList();
    }

    @Override
    @Transactional
    public long createOneBot(OneBotInstanceSaveRequest request) {
        requireUnique(null, request.instanceId(), BotPlatform.ONEBOT, request.botAccountId());
        BotChannelInstance instance = new BotChannelInstance();
        instance.setPlatform(BotPlatform.ONEBOT);
        applyCommon(instance, request.instanceId(), request.displayName(),
                request.botAccountId(), request.enabled());
        applyOneBot(instance, request, true);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_CREATE", "BOT_INSTANCE", instance.getId(),
                "创建 OneBot 实例 " + instance.getInstanceId());
        return instance.getId();
    }

    @Override
    @Transactional
    public void updateOneBot(long id, OneBotInstanceSaveRequest request) {
        BotChannelInstance instance = instance(id, BotPlatform.ONEBOT);
        requireUnique(id, request.instanceId(), BotPlatform.ONEBOT, request.botAccountId());
        applyCommon(instance, request.instanceId(), request.displayName(),
                request.botAccountId(), request.enabled());
        applyOneBot(instance, request, false);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_UPDATE", "BOT_INSTANCE", id,
                "修改 OneBot 实例 " + instance.getInstanceId());
    }

    @Override
    @Transactional
    public long createQqOfficial(QqOfficialInstanceSaveRequest request) {
        BusinessAssert.notBlank(request.clientSecret(), CommonErrorCode.PARAM_INVALID,
                "新增 QQ 官方机器人必须填写 Client Secret");
        requireUnique(null, request.instanceId(), BotPlatform.QQ_OFFICIAL, request.appId());
        BotChannelInstance instance = new BotChannelInstance();
        instance.setPlatform(BotPlatform.QQ_OFFICIAL);
        applyCommon(instance, request.instanceId(), request.displayName(),
                request.appId(), request.enabled());
        applyQqOfficial(instance, request, true);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_CREATE", "BOT_INSTANCE", instance.getId(),
                "创建 QQ 官方机器人实例 " + instance.getInstanceId());
        return instance.getId();
    }

    @Override
    @Transactional
    public void updateQqOfficial(long id, QqOfficialInstanceSaveRequest request) {
        BotChannelInstance instance = instance(id, BotPlatform.QQ_OFFICIAL);
        requireUnique(id, request.instanceId(), BotPlatform.QQ_OFFICIAL, request.appId());
        applyCommon(instance, request.instanceId(), request.displayName(),
                request.appId(), request.enabled());
        applyQqOfficial(instance, request, false);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_UPDATE", "BOT_INSTANCE", id,
                "修改 QQ 官方机器人实例 " + instance.getInstanceId());
    }

    @Override
    @Transactional
    public void delete(long id) {
        BotChannelInstance instance = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("机器人实例不存在"));
        repository.delete(instance);
        auditService.record("BOT_INSTANCE_DELETE", "BOT_INSTANCE", id,
                "删除机器人实例 " + instance.getInstanceId());
    }

    @Override
    public OneBotAccountProbeResponse probeOneBot(OneBotAccountProbeRequest request) {
        String url = trimToNull(request.connectionUrl());
        String token = trimToNull(request.accessToken());
        if (request.instanceId() != null) {
            BotChannelInstance instance = instance(request.instanceId(), BotPlatform.ONEBOT);
            OneBotChannelConfig config = instance.getOneBotConfig();
            if (url == null) {
                url = config.getConnectionUrl();
            }
            if (token == null) {
                token = credentialCipher.decrypt(config.getAccessToken());
            }
        }
        BusinessAssert.notBlank(url, CommonErrorCode.PARAM_INVALID,
                "OneBot WebSocket 地址不能为空");
        try {
            return new OneBotAccountProbeResponse(accountProbe.probe(url, token));
        } catch (OneBotAccountProbeException exception) {
            throw new BusinessException(
                    BotManagementErrorCode.ACCOUNT_PROBE_FAILED, exception.getMessage(), exception);
        }
    }

    private BotChannelInstanceItem toItem(BotChannelInstance instance) {
        OneBotChannelConfig oneBot = instance.getOneBotConfig();
        QqOfficialChannelConfig official = instance.getQqOfficialConfig();
        BotChannelSnapshot snapshot = registry.find(new BotChannelKey(
                instance.getPlatform(), instance.getBotAccountId())).orElse(null);
        return new BotChannelInstanceItem(
                instance.getId(),
                instance.getPlatform(),
                instance.getInstanceId(),
                instance.getDisplayName(),
                instance.getBotAccountId(),
                instance.isEnabled(),
                oneBot == null ? null : oneBot.getConnectionUrl(),
                oneBot != null && hasText(oneBot.getAccessToken()),
                oneBot == null ? null : oneBot.getReconnectIntervalSeconds(),
                official == null ? null : official.getAppId(),
                official != null && hasText(official.getClientSecret()),
                official == null ? null : official.getTokenUrl(),
                official == null ? null : official.getApiBaseUrl(),
                official == null ? null : official.getGatewayUrl(),
                official == null ? null : official.getIntents(),
                official == null ? null : official.getGatewayConnectTimeoutSeconds(),
                official == null ? null : official.getReconnectDelaySeconds(),
                snapshot == null
                        ? (instance.isEnabled() ? "PENDING_RESTART" : "DISABLED")
                        : snapshot.status().name(),
                snapshot == null ? null : snapshot.statusChangedAt(),
                snapshot == null ? null : snapshot.lastError(),
                instance.getCreatedAt(),
                instance.getUpdatedAt());
    }

    private void applyOneBot(BotChannelInstance instance,
                             OneBotInstanceSaveRequest request,
                             boolean create) {
        OneBotChannelConfig config = instance.getOneBotConfig();
        if (config == null) {
            config = new OneBotChannelConfig();
            instance.setOneBotConfig(config);
        }
        config.setConnectionUrl(request.connectionUrl().trim());
        if (create || hasText(request.accessToken())) {
            config.setAccessToken(credentialCipher.encrypt(trimToNull(request.accessToken())));
        }
        config.setReconnectIntervalSeconds(request.reconnectIntervalSeconds());
    }

    private void applyQqOfficial(BotChannelInstance instance,
                                 QqOfficialInstanceSaveRequest request,
                                 boolean create) {
        QqOfficialChannelConfig config = instance.getQqOfficialConfig();
        if (config == null) {
            config = new QqOfficialChannelConfig();
            instance.setQqOfficialConfig(config);
        }
        config.setAppId(request.appId().trim());
        if (create || hasText(request.clientSecret())) {
            config.setClientSecret(credentialCipher.encrypt(trimToNull(request.clientSecret())));
        }
        config.setTokenUrl(request.tokenUrl().trim());
        config.setApiBaseUrl(request.apiBaseUrl().trim());
        config.setGatewayUrl(trimToNull(request.gatewayUrl()));
        config.setIntents(request.intents());
        config.setGatewayConnectTimeoutSeconds(request.gatewayConnectTimeoutSeconds());
        config.setReconnectDelaySeconds(request.reconnectDelaySeconds());
    }

    private void applyCommon(BotChannelInstance instance,
                             String instanceId,
                             String displayName,
                             String botAccountId,
                             boolean enabled) {
        instance.setInstanceId(instanceId.trim());
        instance.setDisplayName(displayName.trim());
        instance.setBotAccountId(botAccountId.trim());
        instance.setEnabled(enabled);
    }

    private void requireUnique(Long currentId,
                               String instanceId,
                               BotPlatform platform,
                               String botAccountId) {
        repository.findByInstanceIdIgnoreCase(instanceId.trim())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new BusinessException(CommonErrorCode.DATA_CONFLICT, "实例标识已存在");
                });
        repository.findByPlatformAndBotAccountId(platform, botAccountId.trim())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new BusinessException(CommonErrorCode.DATA_CONFLICT, "机器人账号已存在");
                });
    }

    private BotChannelInstance instance(long id, BotPlatform expectedPlatform) {
        BotChannelInstance instance = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("机器人实例不存在"));
        BusinessAssert.equals(instance.getPlatform(), expectedPlatform,
                CommonErrorCode.PARAM_INVALID, "机器人实例协议不匹配");
        return instance;
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
