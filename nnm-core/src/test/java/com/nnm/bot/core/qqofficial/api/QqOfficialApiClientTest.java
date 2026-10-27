package com.nnm.bot.core.qqofficial.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.common.http.ExternalHttpException;
import java.util.List;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageSource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

class QqOfficialApiClientTest {

    @Test
    void mediaRejectionCodeDoesNotAuthorizeReplacementWithoutZeroAcceptanceGuarantee() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens,
                new QqOfficialConnection("app", "secret", "http://localhost/token", "http://localhost/api", null));
        QqOfficialMessageSource source = new QqOfficialMessageSource("GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP,
                "group", "message", "app", "{}", null);
        when(tokens.getAccessToken()).thenReturn("token");
        when(http.postJson(anyString(), any(), anyMap(), eq(JsonNode.class)))
                .thenThrow(new ExternalHttpException("POST", "http://localhost/api", 400,
                        "{\"code\":304082}", null));

        try (PreparedBotMessage prepared = client.prepare(source,
                List.of(new BotMessagePart.Markdown(com.nnm.bot.core.message.model.BotMarkdownMessage.content("rich"))),
                BotMessageSendPermit.unrestricted())) {
            assertThatThrownBy(prepared::send)
                    .isInstanceOfSatisfying(BotMessageRejectedException.class,
                            failure -> assertThat(failure.sameInstanceTextFallbackAllowed()).isFalse());
        }
    }

    @Test
    void genericQqRejectionDoesNotAuthorizeAlternateSend() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        QqOfficialApiClient client = new QqOfficialApiClient(http, new ObjectMapper(), tokens,
                new QqOfficialConnection("app", "secret", "http://localhost/token", "http://localhost/api", null));
        QqOfficialMessageSource source = new QqOfficialMessageSource("GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP,
                "group", "message", "app", "{}", null);
        when(tokens.getAccessToken()).thenReturn("token");
        when(http.postJson(anyString(), any(), anyMap(), eq(JsonNode.class)))
                .thenThrow(new ExternalHttpException("POST", "http://localhost/api", 403,
                        "{\"code\":304082}", null));

        try (PreparedBotMessage prepared = client.prepare(source,
                List.of(new BotMessagePart.Markdown(com.nnm.bot.core.message.model.BotMarkdownMessage.content("rich"))),
                BotMessageSendPermit.unrestricted())) {
            assertThatThrownBy(prepared::send)
                    .isInstanceOfSatisfying(BotMessageRejectedException.class,
                            failure -> assertThat(failure.sameInstanceTextFallbackAllowed()).isFalse());
        }
    }

    @Test
    void successHttpWithMediaErrorCodeDoesNotAuthorizeReplacement() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens,
                new QqOfficialConnection("app", "secret", "http://localhost/token", "http://localhost/api", null));
        QqOfficialMessageSource source = new QqOfficialMessageSource("GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP,
                "group", "message", "app", "{}", null);
        when(tokens.getAccessToken()).thenReturn("token");
        when(http.postJson(anyString(), any(), anyMap(), eq(JsonNode.class)))
                .thenReturn(mapper.createObjectNode().put("code", 304083));

        try (PreparedBotMessage prepared = client.prepare(source,
                List.of(new BotMessagePart.Markdown(com.nnm.bot.core.message.model.BotMarkdownMessage.content("rich"))),
                BotMessageSendPermit.unrestricted())) {
            assertThatThrownBy(prepared::send)
                    .isInstanceOfSatisfying(BotMessageRejectedException.class,
                            failure -> assertThat(failure.sameInstanceTextFallbackAllowed()).isFalse());
        }
    }

    @Test
    void sendsMarkdownWithExplicitConfirmKeyboardAsExactlyOnePlatformMessage() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens, new QqOfficialConnection("app", "secret", "http://localhost/token", "http://localhost/api", null));
        QqOfficialMessageSource source = new QqOfficialMessageSource("GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP, "group", "message", "app", "{}", null);
        when(tokens.getAccessToken()).thenReturn("token");
        when(http.postJson(anyString(), any(), anyMap(), eq(JsonNode.class))).thenReturn(mapper.createObjectNode().put("id", "created"));
        try (PreparedBotMessage prepared = client.prepare(source, List.of(new BotMessagePart.Text("标题"),
                new BotMessagePart.Markdown(com.nnm.bot.core.message.model.BotMarkdownMessage.content("**正文**")),
                new BotMessagePart.Keyboard(List.of(new BotMessagePart.Button("下一项", "wm volt_prime", false),
                        new BotMessagePart.Button("Wiki", "https://example.test/wiki", true)))), BotMessageSendPermit.unrestricted())) {
            assertThat(prepared.send().platformMessageId()).isEqualTo("created");
        }
        ArgumentCaptor<ObjectNode> body = ArgumentCaptor.forClass(ObjectNode.class);
        verify(http).postJson(eq("http://localhost/api/v2/groups/group/messages"), body.capture(), anyMap(), eq(JsonNode.class));
        assertThat(body.getValue().path("markdown").path("content").asText()).isEqualTo("标题\n**正文**");
        JsonNode buttons = body.getValue().path("keyboard").path("content").path("rows").get(0).path("buttons");
        assertThat(buttons.get(0).path("action").path("type").asInt()).isEqualTo(2);
        assertThat(buttons.get(0).path("action").path("enter").asBoolean()).isFalse();
        assertThat(buttons.get(0).path("action").path("data").asText()).isEqualTo("wm volt_prime");
        assertThat(buttons.get(1).path("action").path("type").asInt()).isZero();
    }

    @Test
    void rejectsUnsupportedMediaOrderingBeforeUploadOrMessageCreation() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialApiClient client = new QqOfficialApiClient(http, new ObjectMapper(), mock(QqOfficialTokenClient.class),
                new QqOfficialConnection("app", "secret",
                        "http://localhost/token", "http://localhost/api", null));
        QqOfficialMessageSource source = new QqOfficialMessageSource(
                "GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP, "group", "message", "app", "{}", null);
        BotFile image = new BotFile("image.png", "image/png", new byte[] {1});

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> client.prepare(source, List.of(
                        new BotMessagePart.Image(image), new BotMessagePart.Text("after")),
                        BotMessageSendPermit.unrestricted()))
                .isInstanceOf(BotChannelCapabilityException.class);

        verifyNoInteractions(http);
    }

    @Test
    void officialGroupSupportsFileUploadAndRichMediaMessage() {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialConnection connection = new QqOfficialConnection("app", "secret",
                "http://localhost/token", "http://localhost/api", null);
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens, connection);
        QqOfficialMessageSource source = new QqOfficialMessageSource(
                "GROUP_AT_MESSAGE_CREATE", QqOfficialChatType.GROUP, "group", "message", "app", "{}", null);
        BotFile file = new BotFile("report.txt", "text/plain", new byte[] {1, 2, 3});
        ObjectNode prepare = mapper.createObjectNode()
                .put("upload_id", "upload-1")
                .put("block_size", 2);
        ArrayNode parts = prepare.putArray("parts");
        parts.addObject()
                .put("index", 0)
                .put("block_size", 2)
                .put("presigned_url", "https://upload.test/part-0");
        parts.addObject()
                .put("index", 1)
                .put("block_size", 2)
                .put("presigned_url", "https://upload.test/part-1");
        ObjectNode uploaded = mapper.createObjectNode().put("file_info", "file-info-1");

        when(tokens.getAccessToken()).thenReturn("access-token");
        when(http.postJson(anyString(), any(), anyMap(), eq(JsonNode.class)))
                .thenReturn(prepare, mapper.createObjectNode(), mapper.createObjectNode(),
                        uploaded, mapper.createObjectNode().put("id", "qq-created-1"));
        when(http.putBytes(anyString(), any(byte[].class), anyMap()))
                .thenReturn(new HttpClient.HttpResult(200, "", null));

        try (PreparedBotMessage prepared = client.prepare(source, List.of(new BotMessagePart.Text("report"), new BotMessagePart.File(file)),
                BotMessageSendPermit.unrestricted())) {
            verify(http, org.mockito.Mockito.never()).postJson(
                    eq("http://localhost/api/v2/groups/group/messages"), any(), anyMap(), eq(JsonNode.class));
            assertThat(prepared.send().platformMessageId()).isEqualTo("qq-created-1");
        }

        assertThat(BotPlatform.QQ_OFFICIAL.capabilities())
                .contains(BotChannelCapability.FILE_MESSAGE);
        verify(http).putBytes(
                eq("https://upload.test/part-0"),
                argThat(bytes -> Arrays.equals(bytes, new byte[] {1, 2})),
                anyMap());
        verify(http).putBytes(
                eq("https://upload.test/part-1"),
                argThat(bytes -> Arrays.equals(bytes, new byte[] {3})),
                anyMap());
        ArgumentCaptor<ObjectNode> bodyCaptor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(http).postJson(
                eq("http://localhost/api/v2/groups/group/files"),
                bodyCaptor.capture(),
                eq(Map.of("Authorization", "QQBot access-token")),
                eq(JsonNode.class));
        assertThat(bodyCaptor.getValue().path("upload_id").asText()).isEqualTo("upload-1");
        assertThat(bodyCaptor.getValue().path("srv_send_msg").asBoolean()).isFalse();
        verify(http).postJson(
                eq("http://localhost/api/v2/groups/group/messages"),
                bodyCaptor.capture(),
                eq(Map.of("Authorization", "QQBot access-token")),
                eq(JsonNode.class));
        assertThat(bodyCaptor.getValue().path("msg_type").asInt()).isEqualTo(7);
        assertThat(bodyCaptor.getValue().path("media").path("file_info").asText())
                .isEqualTo("file-info-1");
    }

    @Test
    void listsOfficialGroupMembersAcrossPagesAndSkipsBotEntries() throws Exception {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialConnection connection = new QqOfficialConnection("app", "secret",
                "http://localhost/token", "http://localhost/api", null);
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens, connection);
        when(tokens.getAccessToken()).thenReturn("access-token");
        when(http.get(
                eq("http://localhost/api/v2/groups/group-openid/members?cursor="),
                eq(Map.of("Authorization", "QQBot access-token")),
                eq(JsonNode.class)))
                .thenReturn(mapper.readTree("""
                        {"members":[
                          {"member_openid":"member-1","username":"Alice","member_role":"admin","bot":false},
                          {"member_openid":"bot-1","username":"Bot","member_role":"member","bot":true}
                        ],"next_cursor":"next"}
                        """));
        when(http.get(
                eq("http://localhost/api/v2/groups/group-openid/members?cursor=next"),
                eq(Map.of("Authorization", "QQBot access-token")),
                eq(JsonNode.class)))
                .thenReturn(mapper.readTree("""
                        {"members":[
                          {"member_openid":"member-2","username":"Bob","member_role":"owner","bot":false}
                        ],"next_cursor":""}
                        """));

        List<BotDiscoveredMember> members = client.listGroupMembers("group-openid");
        assertThat(members)
                .extracting(member -> member.platformUserId())
                .containsExactly("member-1", "member-2");
        assertThat(members.getFirst().role()).isEqualTo("ADMIN");
    }

    @Test
    void getsOfficialGroupMemberDetail() throws Exception {
        HttpClient http = mock(HttpClient.class);
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        ObjectMapper mapper = new ObjectMapper();
        QqOfficialConnection connection = new QqOfficialConnection("app", "secret",
                "http://localhost/token", "http://localhost/api", null);
        QqOfficialApiClient client = new QqOfficialApiClient(http, mapper, tokens, connection);
        when(tokens.getAccessToken()).thenReturn("access-token");
        when(http.get(
                eq("http://localhost/api/v2/groups/group-openid/members/member-openid"),
                eq(Map.of("Authorization", "QQBot access-token")),
                eq(JsonNode.class)))
                .thenReturn(mapper.readTree(
                        "{\"member_openid\":\"member-openid\",\"username\":\"Alice\","
                                + "\"member_role\":\"owner\"}"));

        BotDiscoveredMember member = client.getGroupMember("group-openid", "member-openid");

        assertThat(member.platformUserId()).isEqualTo("member-openid");
        assertThat(member.displayName()).isEqualTo("Alice");
        assertThat(member.role()).isEqualTo("OWNER");
    }
}
