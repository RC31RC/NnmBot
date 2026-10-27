package com.nnm.bot.warframe.plugin.persistent.official.items;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeEmailVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeKeyVO;
import com.nnm.bot.warframe.persistent.official.service.items.WarframeKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class KeyPluginsTest {
    @Test
    void rejectsInvalidOptionsAndAllowsNumericSearchAndCompletePaths() {
        WarframeKeyService service = mock(WarframeKeyService.class);
        KeysPlugin keys = new KeysPlugin();
        EmailItemsPlugin emails = new EmailItemsPlugin();
        ReflectionTestUtils.setField(keys, "keyService", service);
        ReflectionTestUtils.setField(emails, "keyService", service);
        for (String query : List.of("--page 0", "--page -1", "--page x", "--page", "--page 1 --page 2",
                "--page 1 text", "--kind item", "x".repeat(501))) {
            assertThat(keys.handle(invocation(query)).successful()).isFalse();
            assertThat(emails.handle(invocation(query)).successful()).isFalse();
        }
        verifyNoInteractions(service);
        when(service.keys("1999", 2)).thenReturn(WarframeKeyVO.page("1999", List.of(), 2));
        assertThat(CommandReplyTestData.text(keys.handle(invocation("1999 --page=2")))).contains("没有找到");
        verify(service).keys("1999", 2);
        String fullPath = "/Lotus/" + "A".repeat(300);
        when(service.emails(fullPath, 1)).thenReturn(WarframeEmailVO.page(fullPath, List.of(), 1));
        assertThat(emails.handle(invocation(fullPath)).successful()).isTrue();
        verify(service).emails(fullPath, 1);
    }

    @Test
    void clampsHugePagesAndAcceptsEmptyArguments() {
        WarframeKeyService service = mock(WarframeKeyService.class);
        KeysPlugin keys = new KeysPlugin();
        EmailItemsPlugin emails = new EmailItemsPlugin();
        ReflectionTestUtils.setField(keys, "keyService", service);
        ReflectionTestUtils.setField(emails, "keyService", service);
        when(service.keys("", Integer.MAX_VALUE)).thenReturn(WarframeKeyVO.page("", List.of(), Integer.MAX_VALUE));
        assertThat(((WarframeKeyVO) CommandReplyTestData.data(keys.handle(invocation("--page 99999999999999999999")))).page()).isEqualTo(1);
        when(service.emails("", 1)).thenReturn(WarframeEmailVO.page("", List.of(), 1));
        assertThat(CommandReplyTestData.text(emails.handle(invocation(null)))).contains("邮件定义", "没有找到").doesNotContain("null");
    }

    private CommandInvocation invocation(String query) {
        return new CommandInvocation(mock(BotMessageContext.class), "keys", "钥匙链", query);
    }
}
