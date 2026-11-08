package com.nnm.bot.core.qqofficial.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** QQ Gateway 消息与群成员事件的已知字段；未知上游字段由协议边界忽略，原文另行保留。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QqOfficialGatewayEventDTO(String id, String content, String timestamp,
    @JsonProperty("group_openid") String groupOpenid, @JsonProperty("channel_id") String channelId,
    @JsonProperty("guild_id") String guildId, @JsonProperty("member_openid") String memberOpenid,
    @JsonProperty("member_name") String memberName, String nickname, @JsonProperty("member_role") String memberRole,
    String role, @JsonProperty("session_id") String sessionId,
    @JsonProperty("heartbeat_interval") Long heartbeatInterval, @JsonProperty("message_type") Integer messageType,
    AuthorDTO author, @JsonProperty("msg_elements") List<ReferencedMessageDTO> msgElements,
    @JsonProperty("message_scene") SceneDTO messageScene,
    @JsonProperty("message_reference") MessageReferenceDTO messageReference, List<AttachmentDTO> attachments) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuthorDTO(String id, @JsonProperty("user_openid") String userOpenid,
        @JsonProperty("member_openid") String memberOpenid) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReferencedMessageDTO(@JsonProperty("msg_idx") String msgIdx, List<AttachmentDTO> attachments) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SceneDTO(List<String> ext) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessageReferenceDTO(@JsonProperty("message_id") String messageId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AttachmentDTO(@JsonProperty("content_type") String contentType,
        @JsonProperty("mime_type") String mimeType, String mime, String filename,
        @JsonProperty("file_name") String fileName, String name, String url,
        @JsonProperty("download_url") String downloadUrl, String id, @JsonProperty("file_id") String fileId,
        String type, String size, @JsonProperty("file_size") String fileSize, String width, String height) {
    }
}
