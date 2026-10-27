package com.nnm.bot;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupUpdateDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.service.bot.group.BotGroupManagementService;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Plan;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Transfer;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.support.OfflineRedis;
import io.github.rdt.id.DistributedIdGenerator;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.DistributedLockTemplate;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@OfflineRedis
@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:binding-transaction;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false", "nnm.warframe.pull.polling.enabled=false",
        "shiro.ws.server.enable=false", "shiro.ws.client.enable=false"
})
@MockitoBean(types = {DistributedLockManager.class,
        DistributedLockTemplate.class, DistributedIdGenerator.class})
class BotGroupBindingTransactionTest {
    @Resource
    private BotGroupManagementService service;
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotGroupBotRepository bindings;
    @Resource
    private BotChannelInstanceRepository instances;
    @MockitoBean
    private BotConnectionPlacementCoordinator coordinator;

    @Test
    void refusedGroupActivationAndFailedApprovalKeepTheGroupDisabled() {
        BotGroup group = new BotGroup();
        group.setCode("activation-" + UUID.randomUUID());
        group.setName("activation test");
        group.setEnabled(false);
        groups.save(group);
        doThrow(new BusinessException(BotChannelErrorCode.CONNECTION_MIGRATION_CONFIRMATION_REQUIRED))
                .when(coordinator).prepareGroup(group.getId(), true, null);
        assertThatThrownBy(() -> service.update(group.getId(), new BotGroupUpdateDTO(group.getName(), null, true)))
                .isInstanceOf(BusinessException.class);
        assertThat(groups.findById(group.getId()).orElseThrow().isEnabled()).isFalse();
        Plan plan = new Plan("confirmed", List.of(new Transfer("component", List.of("bot"), null, List.of(), List.of())));
        doReturn(plan).when(coordinator).prepareGroup(group.getId(), true, "confirmed");
        doThrow(new IllegalStateException("approval storage failed")).when(coordinator).approveGroup(plan, group.getId());
        assertThatThrownBy(() -> service.update(group.getId(), new BotGroupUpdateDTO(group.getName(), null, true, "confirmed")))
                .hasMessage("approval storage failed");
        assertThat(groups.findById(group.getId()).orElseThrow().isEnabled()).isFalse();
    }

    @Test
    void missingConfirmationAndApprovalFailureLeaveNoBindingInDatabase() {
        String suffix = UUID.randomUUID().toString();
        BotGroup group = new BotGroup();
        group.setCode("binding-" + suffix);
        group.setName("binding test");
        groups.save(group);
        BotChannelInstance instance = new BotChannelInstance();
        instance.setInstanceId("binding-" + suffix);
        instance.setDisplayName("binding test");
        instance.setPlatform(BotPlatform.ONEBOT);
        instance.setBotAccountId(suffix);
        instances.save(instance);
        BotGroupBotSaveDTO request = new BotGroupBotSaveDTO(instance.getId(), BotPlatform.ONEBOT,
                "GROUP", "group-" + suffix, null, true, false);
        doThrow(new BusinessException(BotChannelErrorCode.CONNECTION_MIGRATION_CONFIRMATION_REQUIRED))
                .when(coordinator).prepare(any(), isNull());
        assertThatThrownBy(() -> service.bindBot(group.getId(), request)).isInstanceOf(BusinessException.class);
        assertThat(bindings.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(group.getId())).isEmpty();
        Plan plan = new Plan("confirmed", List.of(new Transfer("component", List.of("bot"), null, List.of(), List.of())));
        doReturn(plan).when(coordinator).prepare(any(), eq("confirmed"));
        doThrow(new IllegalStateException("approval storage failed")).when(coordinator).approve(eq(plan), anyLong());
        BotGroupBotSaveDTO confirmed = new BotGroupBotSaveDTO(instance.getId(), BotPlatform.ONEBOT,
                request.scopeType(), request.scopeKey(), null, true, false, "confirmed");
        assertThatThrownBy(() -> service.bindBot(group.getId(), confirmed)).hasMessage("approval storage failed");
        assertThat(bindings.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(group.getId())).isEmpty();
    }
}
