package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;

import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.model.PresentationPolicy.Override;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PresentationPolicyTest {
    @Test
    void resolvesExactScopeBeforePlatformAndDefaultWithoutChangingTheOriginal() {
        Override platform = new PresentationPolicy.Override(BotPlatform.QQ_OFFICIAL, null, List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT), CompositionPolicy.PREFER_SINGLE, InteractionPolicy.PREFER_NATIVE);
        Override group = new PresentationPolicy.Override(BotPlatform.QQ_OFFICIAL, BotMessageScope.GROUP, List.of(ViewFormat.IMAGE), CompositionPolicy.ALLOW_SPLIT, InteractionPolicy.COMMAND_TEXT);
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.TEXT), CompositionPolicy.REQUIRE_SINGLE, InteractionPolicy.DISABLED, List.of(platform, group));
        assertThat(policy.resolve(BotPlatform.QQ_OFFICIAL, BotMessageScope.GROUP).viewOrder()).containsExactly(ViewFormat.IMAGE);
        assertThat(policy.resolve(BotPlatform.QQ_OFFICIAL, BotMessageScope.PRIVATE).viewOrder()).containsExactly(ViewFormat.MARKDOWN, ViewFormat.TEXT);
        assertThat(policy.resolve(BotPlatform.ONEBOT, BotMessageScope.GROUP)).isSameAs(policy);
        assertThat(policy.overrides()).hasSize(2);
        assertThatThrownBy(() -> new PresentationPolicy(List.of(ViewFormat.TEXT), CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of(group, group))).hasMessageContaining("Duplicate");
    }
}
