package com.nnm.bot.warframe.plugin.persistent.curated.reference;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TermUpdatePluginTest {

    @Test
    void updateLichesExplainsManualTermImport() {
        CommandResult result = new UpdateLichesPlugin().handle(invocation("更新赤毒"));

        assertThat(CommandReplyTestData.text(result)).contains("通用术语可在管理端刷新", "尚未接入独立赤毒库存",
                "指定 DataSource 来源上传", "人工合并", "不执行更新");
    }

    @Test
    void updateSisterExplainsManualTermImport() {
        CommandResult result = new UpdateSisterPlugin().handle(invocation("更新信条"));

        assertThat(CommandReplyTestData.text(result)).contains("通用术语可在管理端刷新", "尚未接入独立信条库存",
                "指定 DataSource 来源上传", "人工合并", "不执行更新");
    }

    @Test
    void updatePerrinExplainsManualTermImport() {
        CommandResult result = new UpdatePerrinSequencePlugin().handle(invocation("更新终幕"));

        assertThat(CommandReplyTestData.text(result)).contains("通用术语可在管理端刷新", "尚未接入独立终幕库存",
                "指定 DataSource 来源上传", "人工合并", "不执行更新");
    }

    private static CommandInvocation invocation(String input) {
        return new CommandInvocation(mock(BotMessageContext.class), "test", "test", input);
    }
}
