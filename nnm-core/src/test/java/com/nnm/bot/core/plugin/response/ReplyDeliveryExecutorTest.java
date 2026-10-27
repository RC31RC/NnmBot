package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReplyDeliveryExecutorTest {
    private final BotMessageSenderRouter sender = mock(BotMessageSenderRouter.class);
    private final BotMessageSendPermit permit = BotMessageSendPermit.unrestricted();
    private final ReplyDeliveryExecutor executor = new ReplyDeliveryExecutor();
    private final BotMessageContext context = BotMessageContext.of(BotPlatform.ONEBOT,
            BotMessageScope.GROUP, "bot", "user", "group", "receipt", "", "", null);

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(executor, "senders", sender);
    }

    @Test
    void sendsOrderedCompositionInExactlyOneOperation() {
        CommandAttachment image = new CommandAttachment("image.png", "image/png", new byte[]{1});
        executor.prepare(context, plan(List.of(new ReplyBlock.Text("before"), new ReplyBlock.Image(image),
                new ReplyBlock.Text("between"), new ReplyBlock.Image(image))), permit);
        ArgumentCaptor<List<BotMessagePart>> capture = ArgumentCaptor.captor();
        verify(sender).prepare(eq(context), capture.capture(), eq("user"), eq(permit));
        assertThat(capture.getValue()).extracting(Object::getClass).containsExactly(
                BotMessagePart.Text.class, BotMessagePart.Image.class,
                BotMessagePart.Text.class, BotMessagePart.Image.class);
        assertThat(((BotMessagePart.Text) capture.getValue().get(2)).content()).isEqualTo("between");
        verifyNoMoreInteractions(sender);
    }

    @Test
    void protocolFailureNeverTriggersTextFallbackOrReplay() {
        IllegalStateException failure = new IllegalStateException("protocol timeout");
        when(sender.prepare(any(), any(), any(), any())).thenThrow(failure);
        CommandAttachment image = new CommandAttachment("image.png", "image/png", new byte[]{1});
        assertThatThrownBy(() -> executor.prepare(context, plan(List.of(new ReplyBlock.Image(image))), permit))
                .isSameAs(failure);
        verify(sender).prepare(any(), any(), any(), any());
        verifyNoMoreInteractions(sender);
    }

    @Test
    void necessaryFileIsSentWithoutCreatingAnExtraMentionMessage() {
        CommandAttachment file = new CommandAttachment("result.txt", "text/plain", new byte[]{1});
        executor.prepare(context, plan(List.of(new ReplyBlock.File(file))), permit);
        ArgumentCaptor<List<BotMessagePart>> capture = ArgumentCaptor.captor();
        verify(sender).prepare(eq(context), capture.capture(), eq("user"), eq(permit));
        assertThat(capture.getValue()).singleElement().isInstanceOf(BotMessagePart.File.class);
        verifyNoMoreInteractions(sender);
    }

    private ResponsePlan plan(List<ReplyBlock> blocks) {
        return new ResponsePlan(0, 0, blocks, List.of(), "user",
                CompositionPolicy.PREFER_SINGLE, ViewFormat.TEXT, false);
    }
}
