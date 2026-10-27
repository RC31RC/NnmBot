package com.nnm.bot.admin.controller.assembler.bot.scope;

import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberDTO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeDiscoveryMemberVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeDiscoveryScopeVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeDiscoveryVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeMemberVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotScopeViewAssembler {
    private AdminBotScopeViewAssembler() {
    }

    public static BotScopeVO toBotScopeVO(BotScopeDTO value) {
        if (value == null) return null;
        return new BotScopeVO(
                value.id(),
                value.platform(),
                value.scopeType(),
                value.scopeKey(),
                value.botInstanceId(),
                value.instanceId(),
                value.displayName(),
                value.enabled(),
                value.memberCount());
    }

    public static List<BotScopeVO> toBotScopeVO(List<BotScopeDTO> values) {
        return values.stream().map(AdminBotScopeViewAssembler::toBotScopeVO).toList();
    }

    public static BotScopeDiscoveryVO toBotScopeDiscoveryVO(BotScopeDiscoveryDTO value) {
        if (value == null) return null;
        return new BotScopeDiscoveryVO(
                value.botInstanceId(),
                value.instanceId(),
                value.displayName(),
                value.platform(),
                value.runtimeStatus(),
                value.supported(),
                value.message(),
                value.scopes() == null ? null : value.scopes().stream().map(AdminBotScopeViewAssembler::toBotScopeDiscoveryScopeVO).toList());
    }

    public static List<BotScopeDiscoveryVO> toBotScopeDiscoveryVO(List<BotScopeDiscoveryDTO> values) {
        return values.stream().map(AdminBotScopeViewAssembler::toBotScopeDiscoveryVO).toList();
    }

    public static BotScopeDiscoveryMemberVO toBotScopeDiscoveryMemberVO(BotScopeDiscoveryMemberDTO value) {
        if (value == null) return null;
        return new BotScopeDiscoveryMemberVO(
                value.platformUserId(),
                value.displayName(),
                value.memberRole());
    }

    public static BotScopeDiscoveryScopeVO toBotScopeDiscoveryScopeVO(BotScopeDiscoveryScopeDTO value) {
        if (value == null) return null;
        return new BotScopeDiscoveryScopeVO(
                value.scopeType(),
                value.scopeKey(),
                value.displayName(),
                value.memberCount(),
                value.added(),
                value.scopeId(),
                value.members() == null ? null : value.members().stream().map(AdminBotScopeViewAssembler::toBotScopeDiscoveryMemberVO).toList());
    }

    public static BotScopeMemberVO toBotScopeMemberVO(BotScopeMemberDTO value) {
        if (value == null) return null;
        return new BotScopeMemberVO(
                value.id(),
                value.userId(),
                value.platformUserId(),
                value.memberRole(),
                value.enabled());
    }

    public static List<BotScopeMemberVO> toBotScopeMemberVO(List<BotScopeMemberDTO> values) {
        return values.stream().map(AdminBotScopeViewAssembler::toBotScopeMemberVO).toList();
    }

}
