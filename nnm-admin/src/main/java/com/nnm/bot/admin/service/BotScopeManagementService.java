package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotScopeItem;
import com.nnm.bot.admin.dto.bot.BotScopeMemberItem;
import com.nnm.bot.admin.dto.bot.BotScopeMemberUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeCreateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeDiscoveryItem;

import java.util.List;

public interface BotScopeManagementService {

    List<BotScopeItem> scopes();

    List<BotScopeDiscoveryItem> discover();

    long create(BotScopeCreateRequest request);

    void update(long id, BotScopeUpdateRequest request);

    void delete(long id);

    List<BotScopeMemberItem> members(long scopeId);

    void updateMember(long scopeId, long memberId, BotScopeMemberUpdateRequest request);

}
