package com.nnm.bot.admin.service.bot.channel.impl;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Map;
import cn.hutool.core.util.ObjUtil;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.channel.OneBotChannelConfig;
import com.nnm.bot.admin.pojo.entity.bot.channel.QqOfficialChannelConfig;
import com.nnm.bot.admin.pojo.dto.bot.channel.BotChannelInstanceDTO;
import com.nnm.bot.admin.pojo.query.bot.channel.OneBotAccountProbeQuery;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotAccountProbeDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotInstanceSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.QqOfficialInstanceSaveDTO;
import com.nnm.bot.admin.constant.bot.BotManagementErrorCode;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.security.BotCredentialCipher;
import com.nnm.bot.admin.service.bot.channel.BotChannelManagementService;
import com.nnm.bot.admin.service.bot.channel.BotChannelStatusService;
import com.nnm.bot.admin.service.system.OperationAuditService;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.runtime.BotChannelRuntimeManager;
import com.nnm.bot.core.channel.topology.BotTopologyInvalidator;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.onebot.connection.OneBotAccountProbe;
import com.nnm.bot.core.onebot.connection.OneBotAccountProbeException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** 持久化渠道配置并协调运行时实例、拓扑和操作审计。 */
@Service
public class BotChannelManagementServiceImpl implements BotChannelManagementService {

    @Resource
    private BotChannelInstanceRepository repository;
    @Resource
    private BotChannelRuntimeManager runtimeManager;
    @Resource
    private BotTopologyInvalidator topologyInvalidator;
    @Resource
    private OneBotAccountProbe accountProbe;
    @Resource
    private BotCredentialCipher credentialCipher;
    @Resource
    private BotChannelStatusService statusService;
    @Resource
    private OperationAuditService auditService;

    @Override
    public List<BotChannelInstanceDTO> instances() {
        Map<String, BotChannelSnapshot> snapshots = statusService.snapshots().stream()
            .collect(Collectors.toMap(BotChannelSnapshot::instanceId, snapshot -> snapshot));
        return repository.findAllByOrderByPlatformAscInstanceIdAsc().stream()
            .map(instance -> toItem(instance, snapshots.get(instance.getInstanceId()))).toList();
    }

    @Override
    @Transactional
    public long createOneBot(OneBotInstanceSaveDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        requireUnique(null, request.instanceId(), BotPlatform.ONEBOT, request.botAccountId());
        BotChannelInstance instance = new BotChannelInstance();
        instance.setPlatform(BotPlatform.ONEBOT);
        applyCommon(instance, request.instanceId(), request.displayName(), request.botAccountId(), request.enabled());
        applyOneBot(instance, request, true);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_CREATE", "BOT_INSTANCE", instance.getId(),
            "创建 OneBot 实例 " + instance.getInstanceId());
        return instance.getId();
    }

    @Override
    @Transactional
    public void updateOneBot(long id, OneBotInstanceSaveDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        BotChannelInstance instance = instance(id, BotPlatform.ONEBOT);
        String previousInstanceId = instance.getInstanceId();
        requireUnique(id, request.instanceId(), BotPlatform.ONEBOT, request.botAccountId());
        applyCommon(instance, request.instanceId(), request.displayName(), request.botAccountId(), request.enabled());
        applyOneBot(instance, request, false);
        repository.save(instance);
        runtimeManager.remove(previousInstanceId);
        auditService.record("BOT_INSTANCE_UPDATE", "BOT_INSTANCE", id, "修改 OneBot 实例 " + instance.getInstanceId());
    }

    @Override
    @Transactional
    public long createQqOfficial(QqOfficialInstanceSaveDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        BusinessAssert.notBlank(request.clientSecret(), BotManagementErrorCode.QQ_SECRET_REQUIRED);
        requireUnique(null, request.instanceId(), BotPlatform.QQ_OFFICIAL, request.appId());
        BotChannelInstance instance = new BotChannelInstance();
        instance.setPlatform(BotPlatform.QQ_OFFICIAL);
        applyCommon(instance, request.instanceId(), request.displayName(), request.appId(), request.enabled());
        applyQqOfficial(instance, request, true);
        repository.save(instance);
        auditService.record("BOT_INSTANCE_CREATE", "BOT_INSTANCE", instance.getId(),
            "创建 QQ 官方机器人实例 " + instance.getInstanceId());
        return instance.getId();
    }

    @Override
    @Transactional
    public void updateQqOfficial(long id, QqOfficialInstanceSaveDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        BotChannelInstance instance = instance(id, BotPlatform.QQ_OFFICIAL);
        String previousInstanceId = instance.getInstanceId();
        requireUnique(id, request.instanceId(), BotPlatform.QQ_OFFICIAL, request.appId());
        applyCommon(instance, request.instanceId(), request.displayName(), request.appId(), request.enabled());
        applyQqOfficial(instance, request, false);
        repository.save(instance);
        runtimeManager.remove(previousInstanceId);
        auditService.record("BOT_INSTANCE_UPDATE", "BOT_INSTANCE", id, "修改 QQ 官方机器人实例 " + instance.getInstanceId());
    }

    @Override
    @Transactional
    public void delete(long id) {
        topologyInvalidator.invalidateAfterCommit();
        BotChannelInstance instance =
            repository.findById(id).orElseThrow(() -> new BusinessException(BotManagementErrorCode.INSTANCE_NOT_FOUND));
        runtimeManager.remove(instance.getInstanceId());
        repository.delete(instance);
        auditService.record("BOT_INSTANCE_DELETE", "BOT_INSTANCE", id, "删除机器人实例 " + instance.getInstanceId());
    }

    @Override
    public void start(long id) {
        BotChannelInstance instance =
            repository.findById(id).orElseThrow(() -> new BusinessException(BotManagementErrorCode.INSTANCE_NOT_FOUND));
        BusinessAssert.isTrue(instance.isEnabled(), BotManagementErrorCode.INSTANCE_DISABLED);
        try {
            runtimeManager.start(instance.getInstanceId());
        } catch (BusinessException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BusinessException(BotManagementErrorCode.INSTANCE_START_FAILED, exception);
        }
        auditService.record("BOT_INSTANCE_START", "BOT_INSTANCE", id, "启动机器人实例 " + instance.getInstanceId());
    }

    @Override
    public void stop(long id) {
        BotChannelInstance instance =
            repository.findById(id).orElseThrow(() -> new BusinessException(BotManagementErrorCode.INSTANCE_NOT_FOUND));
        runtimeManager.stop(instance.getInstanceId());
        auditService.record("BOT_INSTANCE_STOP", "BOT_INSTANCE", id, "停止机器人实例 " + instance.getInstanceId());
    }

    @Override
    public OneBotAccountProbeDTO probeOneBot(OneBotAccountProbeQuery request) {
        String url = trimToNull(request.connectionUrl());
        String token = trimToNull(request.accessToken());
        if (ObjUtil.isNotNull(request.instanceId())) {
            BotChannelInstance instance = instance(request.instanceId(), BotPlatform.ONEBOT);
            OneBotChannelConfig config = instance.getOneBotConfig();
            if (ObjUtil.isNull(url)) {
                url = config.getConnectionUrl();
            }
            if (ObjUtil.isNull(token)) {
                token = credentialCipher.decrypt(config.getAccessToken());
            }
        }
        BusinessAssert.notBlank(url, BotManagementErrorCode.ONEBOT_URL_REQUIRED);
        try {
            return new OneBotAccountProbeDTO(accountProbe.probe(url, token));
        } catch (OneBotAccountProbeException exception) {
            throw new BusinessException(BotManagementErrorCode.ACCOUNT_PROBE_FAILED, exception);
        }
    }

    private BotChannelInstanceDTO toItem(BotChannelInstance instance, BotChannelSnapshot snapshot) {
        OneBotChannelConfig oneBot = instance.getOneBotConfig();
        QqOfficialChannelConfig official = instance.getQqOfficialConfig();
        return new BotChannelInstanceDTO(instance.getId(), instance.getPlatform(), instance.getInstanceId(),
            instance.getDisplayName(), instance.getBotAccountId(), instance.isEnabled(),
            ObjUtil.isNull(oneBot) ? null : oneBot.getConnectionUrl(),
            ObjUtil.isNotNull(oneBot) && hasText(oneBot.getAccessToken()),
            ObjUtil.isNull(oneBot) ? null : oneBot.getReconnectIntervalSeconds(),
            ObjUtil.isNull(official) ? null : official.getAppId(),
            ObjUtil.isNotNull(official) && hasText(official.getClientSecret()),
            ObjUtil.isNull(official) ? null : official.getTokenUrl(),
            ObjUtil.isNull(official) ? null : official.getApiBaseUrl(),
            ObjUtil.isNull(official) ? null : official.getGatewayUrl(),
            ObjUtil.isNull(official) ? null : official.getIntents(),
            ObjUtil.isNull(official) ? null : official.getGatewayConnectTimeoutSeconds(),
            ObjUtil.isNull(official) ? null : official.getReconnectDelaySeconds(),
            ObjUtil.isNull(snapshot) ? (instance.isEnabled() ? "STOPPED" : "DISABLED") : snapshot.status().name(),
            ObjUtil.isNull(snapshot) ? null : snapshot.statusChangedAt(),
            ObjUtil.isNull(snapshot) ? null : snapshot.lastError(), instance.getCreatedAt(), instance.getUpdatedAt());
    }

    private void applyOneBot(BotChannelInstance instance, OneBotInstanceSaveDTO request, boolean create) {
        OneBotChannelConfig config = instance.getOneBotConfig();
        if (ObjUtil.isNull(config)) {
            config = new OneBotChannelConfig();
            instance.setOneBotConfig(config);
        }
        config.setConnectionUrl(CharSequenceUtil.trim(request.connectionUrl()));
        if (create || hasText(request.accessToken())) {
            config.setAccessToken(credentialCipher.encrypt(trimToNull(request.accessToken())));
        }
        config.setReconnectIntervalSeconds(request.reconnectIntervalSeconds());
    }

    private void applyQqOfficial(BotChannelInstance instance, QqOfficialInstanceSaveDTO request, boolean create) {
        QqOfficialChannelConfig config = instance.getQqOfficialConfig();
        if (ObjUtil.isNull(config)) {
            config = new QqOfficialChannelConfig();
            instance.setQqOfficialConfig(config);
        }
        config.setAppId(CharSequenceUtil.trim(request.appId()));
        if (create || hasText(request.clientSecret())) {
            config.setClientSecret(credentialCipher.encrypt(trimToNull(request.clientSecret())));
        }
        config.setTokenUrl(CharSequenceUtil.trim(request.tokenUrl()));
        config.setApiBaseUrl(CharSequenceUtil.trim(request.apiBaseUrl()));
        config.setGatewayUrl(trimToNull(request.gatewayUrl()));
        config.setIntents(request.intents());
        config.setGatewayConnectTimeoutSeconds(request.gatewayConnectTimeoutSeconds());
        config.setReconnectDelaySeconds(request.reconnectDelaySeconds());
    }

    private void applyCommon(BotChannelInstance instance, String instanceId, String displayName, String botAccountId,
        boolean enabled) {
        instance.setInstanceId(CharSequenceUtil.trim(instanceId));
        instance.setDisplayName(CharSequenceUtil.trim(displayName));
        instance.setBotAccountId(CharSequenceUtil.trim(botAccountId));
        instance.setEnabled(enabled);
    }

    private void requireUnique(Long currentId, String instanceId, BotPlatform platform, String botAccountId) {
        repository.findByInstanceIdIgnoreCase(CharSequenceUtil.trim(instanceId))
            .filter(existing -> !existing.getId().equals(currentId)).ifPresent(existing -> {
                throw new BusinessException(BotManagementErrorCode.INSTANCE_ID_CONFLICT);
            });
        repository.findByPlatformAndBotAccountId(platform, CharSequenceUtil.trim(botAccountId))
            .filter(existing -> !existing.getId().equals(currentId)).ifPresent(existing -> {
                throw new BusinessException(BotManagementErrorCode.ACCOUNT_CONFLICT);
            });
    }

    private BotChannelInstance instance(long id, BotPlatform expectedPlatform) {
        BotChannelInstance instance =
            repository.findById(id).orElseThrow(() -> new BusinessException(BotManagementErrorCode.INSTANCE_NOT_FOUND));
        BusinessAssert.isTrue(instance.getPlatform() == expectedPlatform,
            BotManagementErrorCode.INSTANCE_PLATFORM_MISMATCH);
        return instance;
    }

    private static String trimToNull(String value) {
        return CharSequenceUtil.isBlank(value) ? null : CharSequenceUtil.trim(value);
    }

    private static boolean hasText(String value) {
        return CharSequenceUtil.isNotBlank(value);
    }
}
