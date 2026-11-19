package com.nnm.bot.admin.service.bot.scope.impl;

import com.nnm.bot.admin.constant.bot.BotManagementErrorCode;
import com.nnm.bot.admin.pojo.entity.system.SysUserIdentity;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.scope.BotScope;
import com.nnm.bot.admin.pojo.entity.bot.scope.BotScopeMember;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryScopeDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.repository.bot.scope.BotScopeMemberRepository;
import com.nnm.bot.admin.repository.bot.scope.BotScopeRepository;
import com.nnm.bot.admin.service.bot.scope.BotScopeManagementService;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.runtime.BotScopeDiscoveryRouter;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.admin.repository.system.SysUserIdentityRepository;
import com.nnm.bot.common.error.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 管理人工确认的机器人作用域及其平台成员资料。 */
@Service
public class BotScopeManagementServiceImpl implements BotScopeManagementService {

    @Resource
    private BotScopeRepository scopeRepository;
    @Resource
    private BotScopeMemberRepository memberRepository;
    @Resource
    private BotChannelInstanceRepository instanceRepository;
    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private BotScopeDiscoveryRouter discoveryRouter;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private SysUserIdentityRepository identityRepository;

    @Override
    public List<BotScopeDiscoveryDTO> discover() {
        Map<String, BotChannelInstanceRecord> records = instanceProvider.findAll().stream()
            .collect(Collectors.toMap(BotChannelInstanceRecord::instanceId, Function.identity()));
        return instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc().stream()
            .map(instance -> discovery(instance, records.get(instance.getInstanceId()))).toList();
    }

    @Override
    public List<BotScopeDTO> scopes() {
        return scopeRepository.findAllByOrderByPlatformAscScopeTypeAscScopeKeyAsc().stream().map(this::toItem).toList();
    }

    @Override
    @Transactional
    public long create(BotScopeCreateDTO request) {
        BotChannelInstance instance = instanceRepository.findById(request.botInstanceId())
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.INSTANCE_NOT_FOUND));
        if (instance.getPlatform() != request.platform()) {
            throw new BusinessException(BotManagementErrorCode.SCOPE_PLATFORM_MISMATCH);
        }
        String scopeType = request.scopeType().trim().toUpperCase();
        String scopeKey = request.scopeKey().trim();
        if (scopeRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(request.platform().name(), scopeType,
            scopeKey, instance.getId()).isPresent()) {
            throw new BusinessException(BotManagementErrorCode.SCOPE_CONFLICT);
        }
        BotScope scope = new BotScope();
        scope.setPlatform(request.platform().name());
        scope.setScopeType(scopeType);
        scope.setScopeKey(scopeKey);
        scope.setBotInstanceId(instance.getId());
        scope.setDisplayName(trimToNull(request.displayName()));
        scope.setEnabled(request.enabled());
        BotScope saved = scopeRepository.save(scope);
        if (Boolean.TRUE.equals(request.syncMembers())) {
            synchronizeMembers(saved, request.platform(), scopeType, scopeKey, instance);
        }
        return saved.getId();
    }

    @Override
    @Transactional
    public void update(long id, BotScopeUpdateDTO request) {
        BotScope scope = scope(id);
        scope.setDisplayName(trimToNull(request.displayName()));
        scope.setEnabled(request.enabled());
        scopeRepository.save(scope);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!scopeRepository.existsById(id)) {
            throw new BusinessException(BotManagementErrorCode.GROUP_SCOPE_NOT_FOUND);
        }
        memberRepository.deleteAllByScopeId(id);
        memberRepository.flush();
        scopeRepository.deleteById(id);
    }

    @Override
    public List<BotScopeMemberDTO> members(long scopeId) {
        scope(scopeId);
        return memberRepository.findAllByScopeIdOrderByMemberRoleAscPlatformUserIdAsc(scopeId).stream()
            .map(member -> new BotScopeMemberDTO(member.getId(), member.getUserId(), member.getPlatformUserId(),
                member.getMemberRole(), member.isEnabled()))
            .toList();
    }

    @Override
    @Transactional
    public void updateMember(long scopeId, long memberId, BotScopeMemberUpdateDTO request) {
        scope(scopeId);
        BotScopeMember member = memberRepository.findById(memberId).filter(item -> item.getScopeId().equals(scopeId))
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.GROUP_MEMBER_NOT_FOUND));
        String role = request.memberRole().trim().toUpperCase();
        if (!role.equals("OWNER") && !role.equals("ADMIN") && !role.equals("MEMBER")) {
            throw new BusinessException(BotManagementErrorCode.GROUP_MEMBER_ROLE_INVALID);
        }
        member.setMemberRole(role);
        member.setEnabled(request.enabled());
        memberRepository.save(member);
    }

    private BotScopeDTO toItem(BotScope scope) {
        BotChannelInstance instance = instanceRepository.findById(scope.getBotInstanceId()).orElse(null);
        return new BotScopeDTO(scope.getId(), BotPlatform.valueOf(scope.getPlatform()), scope.getScopeType(),
            scope.getScopeKey(), scope.getBotInstanceId(), ObjectUtil.isNull(instance) ? "-" : instance.getInstanceId(),
            scope.getDisplayName(), scope.isEnabled(), memberRepository.countByScopeId(scope.getId()));
    }

    private BotScopeDiscoveryDTO discovery(BotChannelInstance instance, BotChannelInstanceRecord record) {
        BotChannelSnapshot snapshot =
            channelRegistry.find(new BotChannelKey(instance.getPlatform(), instance.getBotAccountId())).orElse(null);
        BotScopeDiscoveryResult result = ObjectUtil.isNull(record) ? BotScopeDiscoveryResult.unsupported("机器人实例配置不存在")
            : discoveryRouter.discover(record);
        return new BotScopeDiscoveryDTO(instance.getId(), instance.getInstanceId(), instance.getDisplayName(),
            instance.getPlatform(), ObjectUtil.isNull(snapshot) ? null : snapshot.status(), result.supported(),
            result.message(), result.scopes().stream().map(scope -> {
                BotScope existing =
                    scopeRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(instance.getPlatform().name(),
                        scope.scopeType(), scope.scopeKey(), instance.getId()).orElse(null);
                return new BotScopeDiscoveryScopeDTO(scope.scopeType(), scope.scopeKey(), scope.displayName(),
                    scope.memberCount(), ObjectUtil.isNotNull(existing),
                    ObjectUtil.isNull(existing) ? null : existing.getId(),
                    scope.members().stream().map(member -> new BotScopeDiscoveryMemberDTO(member.platformUserId(),
                        member.displayName(), member.role())).toList());
            }).toList());
    }

    private void synchronizeMembers(BotScope scope, BotPlatform platform, String scopeType, String scopeKey,
        BotChannelInstance instance) {
        if (!"GROUP".equals(scopeType)) {
            return;
        }
        BotChannelInstanceRecord record = instanceProvider.findAll().stream()
            .filter(item -> item.instanceId().equals(instance.getInstanceId())).findFirst().orElse(null);
        if (ObjectUtil.isNull(record)) {
            return;
        }
        discoveryRouter.discover(record).scopes().stream()
            .filter(item -> item.scopeType().equals(scopeType) && item.scopeKey().equals(scopeKey)).findFirst()
            .ifPresent(item -> item.members().forEach(member -> {
                SysUserIdentity identity = identityRepository
                    .findByPlatformAndExternalUserId(platform.name(), member.platformUserId()).orElse(null);
                // 未绑定系统用户的 QQ 号只保留在发现结果，不自动创建后台用户。
                if (ObjectUtil.isNull(identity)) {
                    return;
                }
                BotScopeMember entity =
                    memberRepository.findByScopeIdAndPlatformUserId(scope.getId(), member.platformUserId())
                        .orElseGet(BotScopeMember::new);
                entity.setScopeId(scope.getId());
                entity.setUserId(identity.getUserId());
                entity.setPlatformUserId(member.platformUserId());
                entity.setMemberRole(member.role());
                entity.setEnabled(true);
                memberRepository.save(entity);
            }));
    }

    private BotScope scope(long id) {
        return scopeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.GROUP_SCOPE_NOT_FOUND));
    }

    private String trimToNull(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return value.trim();
    }
}
