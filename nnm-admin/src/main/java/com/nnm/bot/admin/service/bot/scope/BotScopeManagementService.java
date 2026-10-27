package com.nnm.bot.admin.service.bot.scope;

import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;

import java.util.List;

public interface BotScopeManagementService {

    List<BotScopeDTO> scopes();

    List<BotScopeDiscoveryDTO> discover();

    long create(BotScopeCreateDTO request);

    void update(long id, BotScopeUpdateDTO request);

    void delete(long id);

    List<BotScopeMemberDTO> members(long scopeId);

    void updateMember(long scopeId, long memberId, BotScopeMemberUpdateDTO request);

}
