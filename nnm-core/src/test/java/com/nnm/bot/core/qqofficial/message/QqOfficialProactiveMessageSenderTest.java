package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class QqOfficialProactiveMessageSenderTest {
    private final QqOfficialProactiveMessageSender sender = new QqOfficialProactiveMessageSender();
    private final BotProactiveTarget target = new BotProactiveTarget(
            BotPlatform.QQ_OFFICIAL, "account", "group-openid", List.of());

    @Test
    void proactiveCapabilitiesDefaultClosedUntilAccountAndGroupAreVerified() {
        ReflectionTestUtils.setField(sender, "replyProperties", new QqReplyProperties());
        assertThat(sender.supports(target, BotChannelCapability.TEXT_MESSAGE)).isFalse();
        assertThat(sender.supports(target, BotChannelCapability.IMAGE_MESSAGE)).isFalse();
        assertThat(sender.supports(target, BotChannelCapability.MARKDOWN_MESSAGE)).isFalse();
        assertThat(sender.supportsComposition(target, List.of(PartKind.TEXT))).isFalse();
    }

    @Test
    void explicitAccountAndGroupQualificationEnablesOnlySupportedCompositions() {
        QqReplyProperties properties = new QqReplyProperties();
        properties.setProactiveGroups(Map.of("account", List.of("group-openid")));
        ReflectionTestUtils.setField(sender, "replyProperties", properties);
        assertThat(sender.supports(target, BotChannelCapability.TEXT_MESSAGE)).isTrue();
        assertThat(sender.supportsComposition(target, List.of(PartKind.MARKDOWN))).isTrue();
        assertThat(sender.supportsComposition(target,
                List.of(PartKind.IMAGE, PartKind.KEYBOARD))).isFalse();
    }

    @Test
    void prepareRejectsBeforeGatewayWhenQualificationIsMissing() {
        ReflectionTestUtils.setField(sender, "replyProperties", new QqReplyProperties());
        assertThatThrownBy(() -> sender.prepare(target, List.of(new BotMessagePart.Text("notice"))))
                .isInstanceOf(BotMessageNotSentException.class)
                .hasMessageContaining("未通过账号和群组资格登记");
    }
}
